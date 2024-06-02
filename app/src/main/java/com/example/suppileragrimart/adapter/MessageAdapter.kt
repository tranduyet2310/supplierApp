package com.example.suppileragrimart.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.MessageItemLeftBinding
import com.example.suppileragrimart.databinding.MessageItemRightBinding
import com.example.suppileragrimart.model.Chat
import com.example.suppileragrimart.utils.Constants
import com.example.suppileragrimart.utils.GlideApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView

class MessageAdapter(
    private val context: Context,
    private val chatList: List<Chat>,
    private val imageUrl: String
) : RecyclerView.Adapter<MessageAdapter.ViewHodler?>() {

    private var firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
    var onViewFullImage: ((Chat) -> Unit)? = null
    inner class ViewHodler(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profileImage: CircleImageView
        var tvMessageContent: TextView
        var tvHadSeen: TextView
        var image: ImageView

        init {
            profileImage = itemView.findViewById(R.id.profileImage)
            tvMessageContent = itemView.findViewById(R.id.tvMessageContent)
            tvHadSeen = itemView.findViewById(R.id.tvHadSeen)
            image = itemView.findViewById(R.id.image)
        }

        fun showImageMessage(chat: Chat){
            tvMessageContent.visibility = View.GONE
            image.visibility = View.VISIBLE
            GlideApp.with(context).load(chat.url).into(image)
        }

        fun showTextMessage(chat: Chat){
            tvMessageContent.text = chat.message
        }

        fun showReceiverImage() {
            val modifiedUrl = imageUrl.replace("http://", "https://")
            GlideApp.with(context).load(modifiedUrl)
                .placeholder(R.drawable.user)
                .error(R.drawable.user)
                .into(profileImage)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHodler {
        return if (viewType == 1) {
            val binding = MessageItemRightBinding.inflate(LayoutInflater.from(context))
            ViewHodler(binding.root)
        } else {
            val binding = MessageItemLeftBinding.inflate(LayoutInflater.from(context))
            ViewHodler(binding.root)
        }
    }

    override fun onBindViewHolder(holder: ViewHodler, position: Int) {
        val currentItem = chatList[position]
        holder.setIsRecyclable(false)
        holder.showReceiverImage()

        if (currentItem.message.equals("Gửi một hình ảnh") && !currentItem.url.equals("")){
            // sender
            if (currentItem.sender.equals(firebaseUser.uid)){
                holder.showImageMessage(currentItem)
                holder.image.setOnClickListener {
                    createSenderOptionDialog(position, holder, currentItem)
                }
            }
            // receiver
            else if (!currentItem.sender.equals(firebaseUser.uid)){
                holder.showImageMessage(currentItem)
                holder.image.setOnClickListener {
                    createReceiverOptionDialog(currentItem)
                }
            }
        } else {
            holder.showTextMessage(currentItem)
            if (currentItem.sender.equals(firebaseUser.uid)){
                holder.tvMessageContent.setOnClickListener {
                    createTextOptionDialog(position, holder)
                }
            }
        }
        // send and had seen message
        if (position == chatList.size-1){
            if (currentItem.isIsseen){
                holder.tvHadSeen.visibility = View.VISIBLE
                setupHadSeenMargin(currentItem, holder)
            } else {
                holder.tvHadSeen.visibility = View.VISIBLE
                holder.tvHadSeen.text = "Đã gửi"
                setupHadSeenMargin(currentItem, holder)
            }
        } else {
            holder.tvHadSeen.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatList[position].sender.equals(firebaseUser.uid)) {
            1
        } else {
            0
        }
    }

    private fun deleteSentMessage(position: Int, holder: ViewHodler){
        FirebaseDatabase.getInstance().reference.child(Constants.CHAT)
            .child(chatList[position].messageId)
            .removeValue()
    }

    private fun setupHadSeenMargin(currentItem: Chat, holder: ViewHodler){
        if (currentItem.message.equals("Gửi một hình ảnh") && !currentItem.url.equals("")){
            val lp : RelativeLayout.LayoutParams? = holder.tvHadSeen.layoutParams as RelativeLayout.LayoutParams?
            lp!!.setMargins(0, 245, 10, 0)
            holder.tvHadSeen.layoutParams = lp
        }
    }

    private fun createSenderOptionDialog(position: Int, holder: ViewHodler, currentItem: Chat){
        val options = arrayOf<CharSequence>(
            "Xem chi tiết",
            "Xóa ảnh",
            "Hủy"
        )
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Thao tác muốn thực hiện?")
        builder.setItems(options) { dialog, which ->
            if (which == 0) {
                onViewFullImage?.invoke(currentItem)
            } else if (which == 1) {
                deleteSentMessage(position, holder)
            }
        }
        builder.show()
    }

    private fun createReceiverOptionDialog(currentItem: Chat){
        val options = arrayOf<CharSequence>(
            "Xem chi tiết",
            "Hủy"
        )
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Thao tác muốn thực hiện?")
        builder.setItems(options) { dialog, which ->
            if (which == 0) {
                onViewFullImage?.invoke(currentItem)
            }
        }
        builder.show()
    }

    private fun createTextOptionDialog(position: Int, holder: ViewHodler){
        val options = arrayOf<CharSequence>(
            "Xoá tin nhắn",
            "Hủy"
        )
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Thao tác muốn thực hiện?")
        builder.setItems(options) { dialog, which ->
            if (which == 0) {
                deleteSentMessage(position, holder)
            }
        }
        builder.show()
    }
}