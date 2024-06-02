package com.example.suppileragrimart.network

import androidx.paging.PagingSource
import com.example.suppileragrimart.model.Product
import com.example.suppileragrimart.model.ProductApiRequest
import retrofit2.HttpException
import java.io.IOException

class ProductPagingSource (
    private val apiService: Api,
    private val productApiRequest: ProductApiRequest,
//    private val secretKey: String,
//    private val iv: String
) : PagingSource<Int, Product>() {
    override val keyReuseSupported: Boolean = true
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        return try {
            val nextPageNumber = params.key ?: 0

            val supplierId = productApiRequest.id
            val sortBy = productApiRequest.sortBy
            val sortDir = productApiRequest.sortDir

            val response = apiService.getProductBySupplierId(
                supplierId,
                nextPageNumber.toString(),
                sortBy,
                sortDir
            )

//            val encryptedData = response.body()?.content
//            val data = decryptData(encryptedData)
            val data = response.body()?.content

            LoadResult.Page(
                data ?: arrayListOf(),
                prevKey = if (nextPageNumber > 0) nextPageNumber - 1 else null,
                nextKey = if (nextPageNumber < response.body()!!.totalPage) nextPageNumber + 1 else null
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }

//    private fun decryptData(products: ArrayList<Product>?): ArrayList<Product>{
//        val aes = AES.getInstance()
//        aes.initFromString(secretKey, iv)
//        val decryptedData: ArrayList<Product> = arrayListOf()
//
//        if (products != null) {
//            for (product in products){
//                val p = Product()
//                p.productId = product.productId
//                Log.d("TEST", "value "+product.productName)
//                p.productName = aes.decrypt(product.productName)
//                p.description = aes.decrypt(product.description)
//                p.standardPrice = product.standardPrice
//                p.discountPrice = product.discountPrice
//                p.productQuantity = product.productQuantity
//                p.productCategory = aes.decrypt(product.productCategory)
//                p.productSubcategory = aes.decrypt(product.productSubcategory)
//                p.warehouseName = aes.decrypt(product.warehouseName)
//                p.productSupplier = aes.decrypt(product.productSupplier)
//                p.productImage = product.productImage
//                p.isActive = product.isActive
//                p.isAvailable = product.isAvailable
//                p.isNew = product.isNew
//                p.supplierProvince = aes.decrypt(product.supplierProvince)
//                p.supplierId = product.supplierId
//                p.sold = product.sold
//
//                decryptedData.add(p)
//            }
//        }
//        return decryptedData
//    }
}