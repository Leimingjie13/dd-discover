package com.example.doordashproject

import com.squareup.moshi.Moshi
import java.lang.Exception
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.Response
import retrofit2.Retrofit

object StoreRepository {

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    private val moshi = Moshi.Builder().add(StoreModelConverter()).build()

    private val server: DoorDashApi by lazy {
        Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(Const.BASE_URL)
            .client(okHttpClient)
            .build()
            .create(DoorDashApi::class.java)
    }

    suspend fun requestList(): MutableList<StoreModel> {
        return server.discover().stores.toMutableList()
    }

    suspend fun requestDetails(storeId: Int): StoreModel {
        return try {
            val response: Response<StoreModel> = server.details(storeId)
            response.body() ?: StoreModel(Const.DEFAULT_ID)
        } catch (e: Exception) {
            ExceptionHandler.displayDialog(e.toString())
            StoreModel(Const.DEFAULT_ID)
        }
    }
}