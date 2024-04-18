package com.example.suppileragrimart.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.suppileragrimart.model.Product
import com.example.suppileragrimart.model.ProductApiRequest
import com.example.suppileragrimart.repository.ProductRepository
import com.example.suppileragrimart.utils.ScreenState
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val productRepository = ProductRepository(application)

    fun getProducts(
        productApiRequest: ProductApiRequest,
        secretKey: String,
        iv: String
    ): Flow<PagingData<Product>> {
        return productRepository.getProductBySupplierId(productApiRequest, secretKey, iv)
            .cachedIn(viewModelScope)
    }

    fun createProduct(
        token: String,
        supplierId: Long,
        product: Product,
        file: List<MultipartBody.Part>
    ): LiveData<ScreenState<Product?>> {
        return productRepository.createProduct(token, supplierId, product, file)
    }
}