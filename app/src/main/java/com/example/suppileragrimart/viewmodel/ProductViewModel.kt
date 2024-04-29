package com.example.suppileragrimart.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.suppileragrimart.model.MessageResponse
import com.example.suppileragrimart.model.Product
import com.example.suppileragrimart.model.ProductApiRequest
import com.example.suppileragrimart.repository.ProductRepository
import com.example.suppileragrimart.utils.ScreenState
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val productRepository = ProductRepository(application)

    fun getProducts(productApiRequest: ProductApiRequest): Flow<PagingData<Product>> {
        return productRepository.getProductBySupplierId(productApiRequest).cachedIn(viewModelScope)
    }

    fun createProduct(
        token: String,
        supplierId: Long,
        product: Product,
        file: List<MultipartBody.Part>
    ): LiveData<ScreenState<Product?>> {
        return productRepository.createProduct(token, supplierId, product, file)
    }

    fun updateProductAll(
        token: String,
        supplierId: Long,
        productId: Long,
        product: Product,
        file: List<MultipartBody.Part>
    ): LiveData<ScreenState<Product?>> {
        return productRepository.updateProductAll(token, supplierId, productId, product, file)
    }

    fun updateProductInfo(token: String, supplierId: Long, productId: Long, product: Product):
            LiveData<ScreenState<Product?>> {
        return productRepository.updateProductInfo(token, supplierId, productId, product)
    }

    fun updateProductState(token: String, productId: Long): LiveData<ScreenState<Product?>> {
        return productRepository.updateProductState(token, productId)
    }

    fun deleteProduct(token: String, supplierId: Long, productId: Long): LiveData<ScreenState<MessageResponse?>> {
        return productRepository.deleteProduct(token, supplierId, productId)
    }
}