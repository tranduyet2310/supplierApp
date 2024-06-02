package com.example.suppileragrimart.view.profile

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.request.RequestOptions
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.FragmentUserAccountBinding
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.utils.Constants
import com.example.suppileragrimart.utils.Constants.FIELD_REQUIRED
import com.example.suppileragrimart.utils.GlideApp
import com.example.suppileragrimart.utils.LoginUtils
import com.example.suppileragrimart.utils.ProgressDialog
import com.example.suppileragrimart.utils.ScreenState
import com.example.suppileragrimart.viewmodel.SupplierViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class UserAccountFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentUserAccountBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController

    private val progressDialog: ProgressDialog by lazy {
        ProgressDialog()
    }
    private val loginUtils: LoginUtils by lazy {
        LoginUtils(requireContext())
    }
    private val supplierViewModel: SupplierViewModel by lazy {
        ViewModelProvider(requireActivity()).get(SupplierViewModel::class.java)
    }
    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firebaseDatabase: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    private lateinit var alertDialog: AlertDialog
    private var supplier: Supplier? = null
    private var imageUri: Uri? = null
    private lateinit var imageActivityResultLauncher: ActivityResultLauncher<Intent>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserAccountBinding.inflate(inflater, container, false)
        binding.toolbarLayout.titleToolbar.text = getString(R.string.account_info)

        supplier = supplierViewModel.supplier

        showInfoData()
        imageActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                imageUri = it.data?.data
                GlideApp.with(requireContext()).load(imageUri).into(binding.imageUser)
                uploadImage()
            }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        binding.tvUpdatePassword.setOnClickListener(this)
        binding.buttonSave.setOnClickListener(this)
        binding.imageEdit.setOnClickListener(this)

        binding.toolbarLayout.imgBack.setOnClickListener {
            navController.navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvUpdatePassword -> goToChangePasswordFragment()
            R.id.buttonSave -> saveInfoUserAccount()
            R.id.imageEdit -> changeAvatar()
        }
    }

    private fun changeAvatar() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        imageActivityResultLauncher.launch(intent)
    }

    private fun saveInfoUserAccount() {
        val newFullName = binding.edtName.text.toString().trim()
        val newPhone = binding.edtPhoneNumber.text.toString().trim()

        if (newFullName.isEmpty() || newPhone.isEmpty()) {
            Snackbar.make(requireView(), FIELD_REQUIRED, Snackbar.LENGTH_SHORT).show()
        } else {
            val supplierDto = Supplier()
            supplierDto.phone = newPhone
            supplierDto.contactName = newFullName
            val token = loginUtils.getSupplierToken()

            supplierViewModel.updateAccountInfo(token, supplier!!.id, supplierDto).observe(
                requireActivity(), { state -> processAccountInfo(state) }
            )
        }
//        navController.navigate(R.id.action_userAccountFragment_to_profileFragment)
    }

    private fun goToChangePasswordFragment() {
        navController.navigate(R.id.action_userAccountFragment_to_changePasswordFragment)
    }

    private fun showInfoData() {
        if (supplier?.avatar != null) showSupplierAvatar(supplier!!.avatar)
        if (supplier?.contactName != null) {
            binding.edtName.text = Editable.Factory.getInstance().newEditable(supplier?.contactName)
        }
        if (supplier?.phone != null) {
            binding.edtPhoneNumber.text =
                Editable.Factory.getInstance().newEditable(supplier?.phone)
        }
        if (supplier?.email != null) {
            binding.edtEmail.text = Editable.Factory.getInstance().newEditable(supplier?.email)
        }
    }

    private fun showSupplierAvatar(imageUrl: String) {
        val modifiedUrl = imageUrl.replace("http://", "https://")
        val requestOptions = RequestOptions().placeholder(R.drawable.user).error(R.drawable.user)
        GlideApp.with(requireContext()).load(modifiedUrl).apply(requestOptions).into(binding.imageUser)
    }

    private fun uploadImage() {
        if (imageUri != null) {
            val imageFile = uriToFile(requireContext(), imageUri!!)
            val image = fileToMultipartBodyPart(imageFile)
            val token = loginUtils.getSupplierToken()

            supplierViewModel.changeSupplierAvatar(token, supplier!!.id, image).observe(
                requireActivity(), { state -> processSupplierAvatar(state) }
            )
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "temp_image.jpg")
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    private fun fileToMultipartBodyPart(file: File): MultipartBody.Part {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("file", file.name, requestFile)
    }

    private fun processSupplierAvatar(state: ScreenState<Supplier?>) {
        when (state) {
            is ScreenState.Loading -> {
                alertDialog = progressDialog.createAlertDialog(requireActivity())
            }

            is ScreenState.Success -> {
                if (state.data != null) {
                    alertDialog.dismiss()
                    supplier!!.avatar = state.data.avatar
                    loginUtils.saveSupplierInfo(supplier!!)
                    supplierViewModel.supplier = supplier
                    updateProfileImageInFirebase(supplier!!.avatar)
                    Snackbar.make(
                        requireView(),
                        getString(R.string.update_avatar),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

            is ScreenState.Error -> {
                alertDialog.dismiss()
                if (state.message != null) {
                    displayErrorSnackbar(state.message)
                }
            }
        }
    }

    private fun processAccountInfo(state: ScreenState<Supplier?>) {
        when (state) {
            is ScreenState.Loading -> {
                alertDialog = progressDialog.createAlertDialog(requireActivity())
            }

            is ScreenState.Success -> {
                if (state.data != null) {
                    alertDialog.dismiss()
                    supplier!!.contactName = state.data.contactName
                    supplier!!.phone = state.data.phone
                    loginUtils.saveSupplierInfo(supplier!!)
                    supplierViewModel.supplier = supplier
                    updateNameInFirebase(supplier!!.contactName)
                    Snackbar.make(
                        requireView(),
                        getString(R.string.update_success),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

            is ScreenState.Error -> {
                alertDialog.dismiss()
                if (state.message != null) {
                    displayErrorSnackbar(state.message)
                }
            }
        }
    }

    private fun updateNameInFirebase(fullName: String){
        val obj = HashMap<String, Any>()
        obj["name"] = fullName
        firebaseDatabase.reference.child(Constants.SUPPLIER).child(auth.uid!!)
            .updateChildren(obj).addOnSuccessListener { }
    }

    private fun updateProfileImageInFirebase(imageUrl: String){
        val obj = HashMap<String, Any>()
        obj["profileImage"] = imageUrl
        firebaseDatabase.reference.child(Constants.SUPPLIER).child(auth.uid!!)
            .updateChildren(obj).addOnSuccessListener { }
    }

    private fun displayErrorSnackbar(errorMessage: String) {
        Snackbar.make(requireView(), errorMessage, Snackbar.LENGTH_INDEFINITE)
            .apply { setAction(Constants.RETRY) { dismiss() } }
            .show()
    }
}