package com.example.suppileragrimart.view.loginRegister

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.ActivitySignUpBinding
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.utils.Constants.FIELD_REQUIRED
import com.example.suppileragrimart.utils.Constants.SUPPLIER
import com.example.suppileragrimart.utils.LoginUtils
import com.example.suppileragrimart.utils.RSA
import com.example.suppileragrimart.utils.Validation

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    private var supplier: Supplier? = null
    private val loginUtils: LoginUtils by lazy {
        LoginUtils(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supplier = intent.getParcelableExtra(SUPPLIER)
        if (supplier != null) {
            showInfo()
        } else supplier = Supplier()


        binding.tvLogin.setOnClickListener {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
        }
        binding.btnContinue.setOnClickListener {
            if (!validateField()) {
                return@setOnClickListener
            }
            generateRSAKey()
            val intent = Intent(applicationContext, MoreInfoSignUpActivity::class.java)
            intent.putExtra(SUPPLIER, supplier)
            startActivity(intent)
        }
    }

    private fun showInfo() {
        binding.edtUserName.text = Editable.Factory.getInstance().newEditable(supplier!!.contactName)
        binding.edtShopName.text = Editable.Factory.getInstance().newEditable(supplier!!.shopName)
        binding.edtUserEmail.text = Editable.Factory.getInstance().newEditable(supplier!!.email)
        binding.edtPhoneNumber.text = Editable.Factory.getInstance().newEditable(supplier!!.phone)
        binding.edtCMND.text = Editable.Factory.getInstance().newEditable(supplier!!.cccd)
        binding.edtTax.text = Editable.Factory.getInstance().newEditable(supplier!!.tax_number)
        binding.edtProvince.text = Editable.Factory.getInstance().newEditable(supplier!!.province)
        binding.edtUserPassword.text = Editable.Factory.getInstance().newEditable(supplier!!.password)
    }

    private fun validateField(): Boolean {
        val contactName = binding.edtUserName.text.toString().trim()
        val shopName = binding.edtShopName.text.toString().trim()
        val email = binding.edtUserEmail.text.toString().trim()
        val phone = binding.edtPhoneNumber.text.toString().trim()
        val cccd = binding.edtCMND.text.toString().trim()
        val taxNumber = binding.edtTax.text.toString().trim()
        val province = binding.edtProvince.text.toString().trim()
        val password = binding.edtUserPassword.text.toString().trim()

        if (contactName.isEmpty() || shopName.isEmpty() || email.isEmpty()
            || phone.isEmpty() || cccd.isEmpty() || taxNumber.isEmpty()
            || province.isEmpty() || password.isEmpty()
        ) {
            displayErrorSnackbar(FIELD_REQUIRED)
            return false
        }

        if (!Validation.isValidName(contactName)) {
            binding.edtUserName.setError(getString(R.string.name_mininum_3_characters))
            binding.edtUserName.requestFocus()
            return false
        }

        if (!Validation.isValidShopName(shopName)) {
            binding.edtShopName.setError(getString(R.string.name_minimun_8_characters_))
            binding.edtShopName.requestFocus()
            return false
        }

        if (!Validation.isValidEmail(email)) {
            binding.edtUserEmail.setError(getString(R.string.format_invalid))
            binding.edtUserEmail.requestFocus()
            return false
        }

        if (!Validation.isValidPhone(phone)) {
            binding.edtPhoneNumber.setError(getString(R.string.phone_invalid))
            binding.edtPhoneNumber.requestFocus()
            return false
        }

        if (!Validation.isValidCCCD(cccd)) {
            binding.edtCMND.setError(getString(R.string.cccd_invalid))
            binding.edtCMND.requestFocus()
            return false
        }

        if (!Validation.isValidTaxNumber(taxNumber)) {
            binding.edtTax.setError(getString(R.string.taxNumber_invalid))
            binding.edtTax.requestFocus()
            return false
        }

        if (!Validation.isValidProvince(province)) {
            binding.edtProvince.setError(getString(R.string.province_invalid))
            binding.edtProvince.requestFocus()
            return false
        }

        if (!Validation.isValidPassword(password)) {
            binding.edtUserPassword.setError(getString(R.string.password_invalid))
            binding.edtUserPassword.requestFocus()
            return false
        }

        supplier!!.contactName = contactName
        supplier!!.shopName = shopName
        supplier!!.email = email
        supplier!!.phone = phone
        supplier!!.cccd = cccd
        supplier!!.tax_number = taxNumber
        supplier!!.province = province
        supplier!!.password = password

        return true
    }

    private fun displayErrorSnackbar(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun generateRSAKey() {
        val rsa = RSA()
        rsa.init()
        supplier!!.rsaPublicKey = rsa.publicKey
        loginUtils.saveRSAKey(rsa.privateKey, rsa.publicKey)
    }
}
