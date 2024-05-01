package com.example.suppileragrimart.view

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.ActivityMainBinding
import com.example.suppileragrimart.model.AESResponse
import com.example.suppileragrimart.model.RsaKey
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.network.Api
import com.example.suppileragrimart.network.RetrofitClient
import com.example.suppileragrimart.utils.LoginUtils
import com.example.suppileragrimart.utils.ProgressDialog
import com.example.suppileragrimart.utils.RSA
import com.example.suppileragrimart.utils.RsaKeyDatabase
import com.example.suppileragrimart.viewmodel.SupplierViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val loginUtils: LoginUtils by lazy {
        LoginUtils(applicationContext)
    }
    private val progressDialog: ProgressDialog by lazy {
        ProgressDialog()
    }
    private val apiService: Api by lazy {
        RetrofitClient.getInstance().getApi()
    }
    private val supplierViewModel: SupplierViewModel by lazy {
        ViewModelProvider(this).get(SupplierViewModel::class.java)
    }

    private lateinit var supplier: Supplier
    private lateinit var alertDialog: AlertDialog
    private  var isValidPubKey: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavHost()

        supplier = loginUtils.getSupplierInfo()
        supplierViewModel.supplier = supplier

        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main){
                alertDialog = progressDialog.createAlertDialog(this@MainActivity)
            }

            checkRSAPublicKey()
            getAESKey()
//            if (isValidPubKey) {
//                supplierViewModel.isValidPublicKey = isValidPubKey
//                getAESKey()
//            }

            withContext(Dispatchers.Main){
                alertDialog.dismiss()
            }
        }


    }

    suspend fun getAESKey() {
        withContext(Dispatchers.IO) {
            val aesResponse = AESResponse()
            aesResponse.rsaPublicKey = loginUtils.getRSAPublicKey()
            val response = apiService.getSessionKey(aesResponse)
            if (response.isSuccessful) {
                val result = response.body()
                if (result != null) {
                    val rsa = RSA()
                    val rsaPrivateKeyInString = loginUtils.getRSAPrivateKey()
                    val rsaPublicKeyInString = loginUtils.getRSAPublicKey()
                    val rsaPrivateKey = rsa.getOriginalPrivateKey(rsaPrivateKeyInString)
                    val rsaPublicKey = rsa.getOriginalPublicKey(rsaPublicKeyInString)
                    rsa.setPrivateKey(rsaPrivateKey)
                    rsa.setPublicKey(rsaPublicKey)

                    val decryptAESKey = rsa.decrypt(result.aesKey)
                    val decryptIv = rsa.decrypt(result.iv)
                    val decryptAESResponse = AESResponse()
                    decryptAESResponse.aesKey = decryptAESKey
                    decryptAESResponse.iv = decryptIv
                    decryptAESResponse.rsaPublicKeyServer = result.rsaPublicKeyServer

                    loginUtils.saveResponseKeys(decryptAESResponse)
                }
            }
        }
    }

    suspend fun checkRSAPublicKey() {
        val rsaKeyInDb = RsaKeyDatabase.getDatabase(this).rsaKeyDao().getRsaKey(supplier.email)
        if (rsaKeyInDb == null){
            withContext(Dispatchers.Main){
                showSnackbar(getString(R.string.pub_key_not_found))
            }
            updateNewKey(false)
        } else {
            val rsaKeyInServer = getRSAPublicKeyInServer()
            val publicKeyInDb = rsaKeyInDb.publicKey
            if (rsaKeyInServer.isEmpty()){
                withContext(Dispatchers.Main){
                    showSnackbar("Không tìm thấy public key trên server")
                }
            } else {
                if (!rsaKeyInServer.equals(publicKeyInDb)){
                    updateNewKey(true)
                }else {
                    if (!publicKeyInDb.equals(loginUtils.getRSAPublicKey())){
                        loginUtils.saveRSAKey(rsaKeyInDb.privateKey, publicKeyInDb)
                        withContext(Dispatchers.Main){
                            showSnackbar("Thay thế public key thành công")
                        }
                    } else {
                        withContext(Dispatchers.Main){
                            showSnackbar("Public Key khớp")
                        }
                    }
                }
            }
        }
    }

    suspend fun getRSAPublicKeyInServer(): String{
        return withContext(Dispatchers.IO){
            val response = apiService.getRSAPublicKey(supplier.id)
            if (response.isSuccessful){
                response.body()?.rsaPublicKey ?: ""
            } else {
                ""
            }
        }
    }

    suspend fun updateNewKey(isUpdate: Boolean){
        generateRSAKey()
        val token = loginUtils.getSupplierToken()
        val dto = AESResponse()
        dto.rsaPublicKey = supplier.rsaPublicKey
        withContext(Dispatchers.IO){
            val response = apiService.updateRSAKey(token, supplier.id, dto)
            if (response.isSuccessful){
                if (response.body() != null){
                    if (response.body()!!.isSuccessful){
                        saveKeyInDb(isUpdate)
                        Log.d("TEST","Luu khoa vao Room")
                    }
                }
            }
        }
    }
    private fun saveKeyInDb(isUpdate: Boolean){
        val rsaPrivateKeyInString = loginUtils.getRSAPrivateKey()
        val rsaPublicKeyInString = loginUtils.getRSAPublicKey()
        if (isUpdate){
            val rsaKeyInDb = RsaKeyDatabase.getDatabase(this).rsaKeyDao().getRsaKey(supplier.email)
            if (rsaKeyInDb != null) {
                rsaKeyInDb.publicKey = rsaPublicKeyInString
                rsaKeyInDb.privateKey = rsaPrivateKeyInString
                RsaKeyDatabase.getDatabase(this).rsaKeyDao().updateRsaKey(rsaKeyInDb)
            }
        } else {
            val rsaKey = RsaKey(0, supplier.email, rsaPublicKeyInString, rsaPrivateKeyInString)
            RsaKeyDatabase.getDatabase(this).rsaKeyDao().addRsaKey(rsaKey)
        }
    }
    private fun generateRSAKey() {
        val rsa = RSA()
        rsa.init()
        supplier.rsaPublicKey = rsa.publicKey
        loginUtils.saveRSAKey(rsa.privateKey, rsa.publicKey)
    }

    private fun setupNavHost(){
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        val bottomNavigationView = binding.bottomNavigationView
        setupWithNavController(bottomNavigationView, navController)

        setSupportActionBar(findViewById(R.id.toolbar))
        val config = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, config)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment, R.id.profileFragment -> supportActionBar?.hide()
                else -> {
                    supportActionBar?.hide()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.notification_menu, menu)
        val menuItem = menu!!.findItem(R.id.notificationFragment)
        val actionView = menuItem.actionView

        val notificationBadge = actionView?.findViewById<TextView>(R.id.tvCartBadge)
        notificationBadge?.text = "3"

        actionView?.setOnClickListener {
            onOptionsItemSelected(menuItem)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Toast.makeText(applicationContext, "Clicked", Toast.LENGTH_SHORT).show()
        return super.onOptionsItemSelected(item)
    }

    private fun showSnackbar(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}