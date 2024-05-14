package com.example.suppileragrimart.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.SupplierSearchListItemBinding
import com.example.suppileragrimart.model.Chat
import com.example.suppileragrimart.model.UserFirebase
import com.example.suppileragrimart.utils.Constants
import com.example.suppileragrimart.utils.GlideApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatAdapter(
    context: Context,
    userList: List<UserFirebase>,
    isChatCheck: Boolean
) : RecyclerView.Adapter<ChatAdapter.ViewHolder?>() {

    private val context: Context
    private val userList: List<UserFirebase>
    private val isChatCheck: Boolean
    var onClick: ((UserFirebase) -> Unit)? = null
    var lastTextMessage: String = ""

    init {
        this.context = context
        this.userList = userList
        this.isChatCheck = isChatCheck
    }

    class ViewHolder(
        binding: SupplierSearchListItemBinding,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {
        val tvUserName = binding.tvUserName
        val profileImage = binding.profileImage
        val imgOnline = binding.imgOnline
        val imgOffline = binding.imgOffline
        val lastMessage = binding.tvLastMessage

        fun bind(userFirebase: UserFirebase){
            tvUserName.text = userFirebase.name
            val imageUrl = userFirebase.profileImage
            val modifiedUrl = imageUrl.replace("http://", "https://")
            GlideApp.with(context).load(modifiedUrl)
                .placeholder(R.drawable.user)
                .error(R.drawable.user)
                .skipMemoryCache(true)
                .into(profileImage)
        }

        fun showUserStatus(userFirebase: UserFirebase){
            if (userFirebase.status.equals("online")){
                imgOnline.visibility = View.VISIBLE
                imgOffline.visibility = View.GONE
            } else {
                imgOnline.visibility = View.GONE
                imgOffline.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(SupplierSearchListItemBinding.inflate(LayoutInflater.from(parent.context)), context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = userList[position]
        holder.bind(currentItem)
        holder.itemView.setOnClickListener {
            onClick?.invoke(currentItem)
        }
        if (isChatCheck){
            holder.showUserStatus(currentItem)
            retrieveLastMessage(currentItem.uid, holder.lastMessage)
        } else {
            holder.lastMessage.visibility = View.GONE
            holder.imgOnline.visibility = View.GONE
            holder.imgOffline.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    private fun retrieveLastMessage(chatUserUid: String?, lastMessage: TextView) {
        lastTextMessage = "defaultMsg"
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().reference.child(Constants.CHAT)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children){
                    val chat: Chat? = dataSnapshot.getValue(Chat::class.java)
                    if (firebaseUser != null && chat != null){
                        if (chat.receiver.equals(firebaseUser.uid) && chat.sender.equals(chatUserUid)
                            || chat.receiver.equals(chatUserUid) && chat.sender.equals(firebaseUser.uid)){
                            lastTextMessage = chat.message
                        }
                    }
                }

                when(lastTextMessage){
                    "defaultMsg" -> lastMessage.text = ""
                    "Gửi một hình ảnh" -> lastMessage.text = lastTextMessage
                    else -> lastMessage.text = lastTextMessage
                }

                lastTextMessage = "defaultMsg"
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}