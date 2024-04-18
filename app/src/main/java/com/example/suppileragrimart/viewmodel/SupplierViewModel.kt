package com.example.suppileragrimart.viewmodel

import androidx.lifecycle.ViewModel
import com.example.suppileragrimart.model.Supplier
import java.security.PrivateKey
import java.security.PublicKey

class SupplierViewModel: ViewModel() {
    var supplier: Supplier? = null
    var isValidPublicKey: Boolean = false
}