package com.example.suppileragrimart.view.profile

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.suppileragrimart.R
import com.example.suppileragrimart.adapter.MessageAdapter
import com.example.suppileragrimart.databinding.FragmentChatDetailBinding
import com.example.suppileragrimart.model.Chat
import com.example.suppileragrimart.model.NotificationMessage
import com.example.suppileragrimart.model.Token
import com.example.suppileragrimart.model.UserFirebase
import com.example.suppileragrimart.network.Api
import com.example.suppileragrimart.network.RetrofitClient
import com.example.suppileragrimart.utils.Constants
import com.example.suppileragrimart.utils.GlideApp
import com.example.suppileragrimart.utils.ProgressDialog
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ChatDetailFragment : Fragment() {
    private lateinit var binding: FragmentChatDetailBinding
    private lateinit var navController: NavController

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firebaseDatabase: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }
    private val firebaseStorage: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    private val progressDialog: ProgressDialog by lazy {
        ProgressDialog()
    }
    private val apiService: Api by lazy {
        RetrofitClient.getInstance().getApi()
    }

    private val navArgs: ChatDetailFragmentArgs by navArgs()
    private lateinit var userUid: String
    private var firebaseUser: FirebaseUser? = null
    private lateinit var alertDialog: AlertDialog
    private lateinit var messageAdapter: MessageAdapter
    private val chatList: List<Chat> = arrayListOf()
    private var seenMessageListener: ValueEventListener? = null
    private lateinit var reference: DatabaseReference
    private var notify = false


    private lateinit var imageActivityResultLauncher: ActivityResultLauncher<Intent>
    private var imageUri: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatDetailBinding.inflate(inflater)

        userUid = navArgs.userUid
        firebaseUser = auth.currentUser

        setupReceiverInfo()
        setupSelectImageEvent()
        seenMessage(userUid)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        binding.imgBack.setOnClickListener {
            navController.navigateUp()
        }

        binding.imgSendMessage.setOnClickListener {
            val message = binding.tvTextMessage.text.toString()
            if (message.equals("")){
                showSnackBar("Chưa nhập nội dung tin nhắn")
            } else {
                notify = true
                sendMessageToUser(firebaseUser!!.uid, userUid, message)
            }
            binding.tvTextMessage.text = Editable.Factory.getInstance().newEditable("")
        }

        binding.imgAttachImg.setOnClickListener {
            notify = true
            selectImage()
        }

    }
    private fun setupSelectImageEvent() {
        imageActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            alertDialog = progressDialog.createAlertDialog(requireActivity())

            imageUri = it.data?.data
            if (imageUri != null){
                val storageReference = firebaseStorage.reference.child(Constants.CHAT_IMAGES)
                val ref = firebaseDatabase.reference
                val messageId = ref.push().key
                val filePath = storageReference.child("$messageId.jpg")

                val uploadTask: StorageTask<*>
                uploadTask = filePath.putFile(imageUri!!)

                uploadTask.continueWithTask<Uri?>(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                    if (task.isSuccessful){
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation filePath.downloadUrl
                }).addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        val downloadUrl = task.result
                        val url = downloadUrl.toString()

                        val messageHashMap = HashMap<String, Any?>()
                        messageHashMap["sender"] = firebaseUser!!.uid
                        messageHashMap["message"] = "Gửi một hình ảnh"
                        messageHashMap["receiver"] = userUid
                        messageHashMap["isseen"] = false
                        messageHashMap["url"] = url
                        messageHashMap["messageId"] = messageId

                        ref.child(Constants.CHAT).child(messageId!!).setValue(messageHashMap)
                            .addOnCompleteListener { tasks ->
                                if (tasks.isSuccessful){
                                    alertDialog.dismiss()

                                    // Implement push notification
                                    val references = firebaseDatabase.reference.child(Constants.SUPPLIER)
                                        .child(firebaseUser!!.uid)
                                    references.addValueEventListener(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            val user = snapshot.getValue(UserFirebase::class.java)
                                            if (notify)
                                            {
                                                sendNotification(userUid, user!!.name, "Gửi một hình ảnh")
                                            }
                                            notify = false
                                        }

                                        override fun onCancelled(error: DatabaseError) {

                                        }
                                    })
                                }
                            }


                    }
                }
            }

        }
    }
    private fun setupReceiverInfo() {
        reference = firebaseDatabase.reference.child(Constants.USER).child(userUid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user: UserFirebase? = snapshot.getValue(UserFirebase::class.java)
                binding.tvUserName.text = user!!.name

                val imageUrl = user.profileImage
                val modifiedUrl = imageUrl.replace("http://", "https://")
                GlideApp.with(requireContext()).load(modifiedUrl)
                    .placeholder(R.drawable.user)
                    .error(R.drawable.user)
                    .into(binding.profileImage)

                setupRecyclerView(imageUrl)
                retrieveMessage(firebaseUser!!.uid, userUid, user.profileImage)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    private fun sendMessageToUser(senderUid: String, receiverUid: String, message: String) {
        val reference = firebaseDatabase.reference
        val messageKey = reference.push().key

        val messageHashMap = HashMap<String, Any?>()
        messageHashMap["sender"] = senderUid
        messageHashMap["message"] = message
        messageHashMap["receiver"] = receiverUid
        messageHashMap["isseen"] = false
        messageHashMap["url"] = ""
        messageHashMap["messageId"] = messageKey

        reference.child(Constants.CHAT)
            .child(messageKey!!)
            .setValue(messageHashMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val chatListReference = firebaseDatabase.reference
                        .child(Constants.CHAT_LIST)
                        .child(firebaseUser!!.uid)
                        .child(userUid)
                    chatListReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(!snapshot.exists()){
                                chatListReference.child("id").setValue(userUid)
                            }

                            val chatListReceiverReference = firebaseDatabase.reference
                                .child(Constants.CHAT_LIST)
                                .child(userUid)
                                .child(firebaseUser!!.uid)
                            chatListReceiverReference.child("id").setValue(firebaseUser!!.uid)
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
                }
            }
        // Implement push notification
        val references = firebaseDatabase.reference.child(Constants.SUPPLIER)
            .child(firebaseUser!!.uid)
        references.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserFirebase::class.java)
                if (notify)
                {
                    sendNotification(receiverUid, user!!.name, message)
                }
                notify = false
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun sendNotification(receiverUid: String, name: String?, message: String) {
        val ref = firebaseDatabase.reference.child(Constants.CHAT_TOKEN)
        val query = ref.orderByKey().equalTo(receiverUid)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children){
                    val token: Token? = dataSnapshot.getValue(Token::class.java)
                    val notificationMessage = NotificationMessage().apply {
                        recipientToken = token!!.token
                        title = "Tin nhắn mới"
                        body = "$name: $message"
                        data = hashMapOf()
                    }
                    pushNotification(notificationMessage)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun pushNotification(notificationMessage: NotificationMessage){
        lifecycleScope.launch {
            withContext(Dispatchers.IO){
                val response = apiService.sendNotification(notificationMessage)
                if (response.isSuccessful){
                    if (response.body() != null){
                        if (response.body()!!.isSuccessful){
                            Log.d("TEST", "Send notification to supplier succesfully")
                        } else {
                            Log.d("TEST", "Failed to send notification")
                        }
                    }
                }
            }
        }
    }

    private fun retrieveMessage(senderUid: String, receiverUid: String, imageUrl: String?) {
        val reference = firebaseDatabase.reference.child(Constants.CHAT)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (chatList as ArrayList<Chat>).clear()
                for (s in snapshot.children){
                    val chat = s.getValue(Chat::class.java)
                    if (chat!!.receiver.equals(senderUid) && chat.sender.equals(receiverUid)
                        || chat.receiver.equals(receiverUid) && chat.sender.equals(senderUid)){
                        (chatList as ArrayList<Chat>).add(chat)
                    }
                    messageAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun setupRecyclerView(imageUrl: String) {
        binding.rcvChat.apply {
            setHasFixedSize(true)
            val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            linearLayoutManager.stackFromEnd = true
            layoutManager = linearLayoutManager
            messageAdapter = MessageAdapter(requireContext(), chatList, imageUrl)
            adapter = messageAdapter
        }

        messageAdapter.onViewFullImage = {
            val b = Bundle().apply {
                putString(Constants.IMAGE_KEY, it.url)
            }
            navController.navigate(R.id.action_chatDetailFragment_to_viewFullImageFragment, b)
        }
    }

    private fun seenMessage(userUid: String){
        val reference = firebaseDatabase.reference.child(Constants.CHAT)
        seenMessageListener = reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (s in snapshot.children){
                    val chat = s.getValue(Chat::class.java)
                    if (chat!!.receiver.equals(firebaseUser!!.uid) && chat.sender.equals(userUid)){
                        val hashMap = HashMap<String, Any>()
                        hashMap["isseen"] = true
                        s.ref.updateChildren(hashMap)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        imageActivityResultLauncher.launch(intent)
    }

    private fun showSnackBar(message: String){
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
        reference.removeEventListener(seenMessageListener!!)
    }
}