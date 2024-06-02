package com.example.suppileragrimart.view.chat

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.suppileragrimart.R
import com.example.suppileragrimart.adapter.ChatAdapter
import com.example.suppileragrimart.databinding.FragmentSearchChatBinding
import com.example.suppileragrimart.model.UserFirebase
import com.example.suppileragrimart.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class SearchChatFragment : Fragment() {
    private lateinit var binding: FragmentSearchChatBinding

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firebaseDatabase: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    private lateinit var chatAdapter: ChatAdapter
    private var userList: List<UserFirebase> = arrayListOf()
    private var firebaseUser: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchChatBinding.inflate(inflater)

        firebaseUser = auth.currentUser
        if (firebaseUser != null){
            setupRecyclerView()
            getAllUsers()
        } else binding.imgBox.visibility = View.VISIBLE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (firebaseUser != null){
            setupEditTextListener()
            chatAdapter.onClick = {
                val b = Bundle().apply {
                    putString(Constants.USER_UID_KEY, it.uid)
                }
                this@SearchChatFragment.findNavController().navigate(R.id.action_messageFragment_to_chatDetailFragment, b)
            }
        }

    }

    private fun setupEditTextListener(){
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchForUser(s.toString().lowercase())
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun getAllUsers() {
        val userUid = auth.currentUser!!.uid
        val reference = firebaseDatabase.reference.child(Constants.USER)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (userList as ArrayList<UserFirebase>).clear()
                if (binding.edtSearch.text.toString() == ""){
                    for (s in snapshot.children){
                        val user: UserFirebase? = s.getValue(UserFirebase::class.java)
                        if (!(user!!.uid).equals(userUid)){
                            (userList as ArrayList<UserFirebase>).add(user)
                        }
                    }
                    chatAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun searchForUser(supplierName: String){
        val userUid = auth.currentUser!!.uid
        val reference = firebaseDatabase.reference.child(Constants.USER).orderByChild("search")
            .startAt(supplierName)
            .endAt(supplierName + "\uf8ff") // \uf8ff = %

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (userList as ArrayList<UserFirebase>).clear()
                for (s in snapshot.children){
                    val user: UserFirebase? = s.getValue(UserFirebase::class.java)
                    if (!(user!!.uid).equals(userUid)){
                        (userList as ArrayList<UserFirebase>).add(user)
                    }
                    chatAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun setupRecyclerView() {
        binding.rcvSearchList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            chatAdapter = ChatAdapter(requireContext(), userList, false)
            adapter = chatAdapter
        }
    }

}