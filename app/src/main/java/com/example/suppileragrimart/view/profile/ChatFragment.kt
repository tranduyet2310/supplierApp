package com.example.suppileragrimart.view.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.suppileragrimart.R
import com.example.suppileragrimart.adapter.ChatAdapter
import com.example.suppileragrimart.databinding.FragmentChatBinding
import com.example.suppileragrimart.model.ChatList
import com.example.suppileragrimart.model.UserFirebase
import com.example.suppileragrimart.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ChatFragment : Fragment() {
    private lateinit var binding: FragmentChatBinding

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firebaseDatabase: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }
    private var firebaseUser: FirebaseUser? = null

    private lateinit var chatAdapter: ChatAdapter
    private var userList: List<UserFirebase> = arrayListOf()
    private var userChatList: List<ChatList> = arrayListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater)

        setupRecyclerView()
        firebaseUser = auth.currentUser
        if (firebaseUser != null)
            getCurrentChatList()
        else binding.imageEmptyBox.visibility = View.VISIBLE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (firebaseUser != null){
            chatAdapter.onClick = {
                val b = Bundle().apply {
                    putString(Constants.USER_UID_KEY, it.uid)
                }
                this@ChatFragment.findNavController().navigate(R.id.action_messageFragment_to_chatDetailFragment, b)
            }
        }

    }
    private fun getCurrentChatList(){
        val ref = firebaseDatabase.reference.child(Constants.CHAT_LIST).child(firebaseUser!!.uid)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (userChatList as ArrayList).clear()
                for (s in snapshot.children){
                    val chatList = s.getValue(ChatList::class.java)
                    (userChatList as ArrayList).add(chatList!!)
                }
                retrieveChatList()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    private fun setupRecyclerView(){
        binding.rcvChatList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            chatAdapter = ChatAdapter(requireContext(), userList, true)
            adapter = chatAdapter
        }
    }
    private fun retrieveChatList(){
        val ref = firebaseDatabase.reference.child(Constants.USER)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (userList as ArrayList).clear()
                for (s in snapshot.children){
                    val user = s.getValue(UserFirebase::class.java)
                    Log.w("TEST", "Check uid: ${user!!.uid}")
                    for (eachChatList in userChatList){
                        if (user!!.uid.equals(eachChatList.id)){
                            (userList as ArrayList).add(user)
                        }
                    }
                }
                chatAdapter.notifyDataSetChanged()
                if (userList.isEmpty()) binding.imageEmptyBox.visibility = View.VISIBLE
                else binding.imageEmptyBox.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}