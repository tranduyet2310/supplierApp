package com.example.suppileragrimart.network

import androidx.paging.PagingSource
import com.example.suppileragrimart.model.Cooperation
import com.example.suppileragrimart.model.CooperationApiRequest
import retrofit2.HttpException
import java.io.IOException

class CooperationPagingSource(
    private val apiService: Api,
    private val supplierId: Long,
    private val cooperationApiRequest: CooperationApiRequest
) : PagingSource<Int, Cooperation>() {
    override val keyReuseSupported: Boolean = true
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Cooperation> {
        return try {
            val nextPageNumber = params.key ?: 0

            val fieldId = cooperationApiRequest.id
            val sortBy = cooperationApiRequest.sortBy
            val sortDir = cooperationApiRequest.sortDir

            val response = apiService.getCooperationBySupplierId(
                supplierId,
                fieldId,
                nextPageNumber.toString(),
                sortBy,
                sortDir
            )
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
}