package com.example.doordashproject

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.Response

private const val LIST_ENDPOINT = "/v1/store_feed/"
private const val DETAILS_ENDPOINT = "/v2/restaurant/"
private const val DEFAULT_LATITUDE = "37.422740"
private const val DEFAULT_LONGITUDE = "-122.139956"
private const val SEARCH_LIMIT = 50

interface DoorDashApi {

    @GET("$LIST_ENDPOINT?")
    suspend fun discover(
        @Query("lat") latitude: String = DEFAULT_LATITUDE,
        @Query("lng") longitude: String = DEFAULT_LONGITUDE,
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = SEARCH_LIMIT,
    ): Stores

    @GET("$DETAILS_ENDPOINT{id}/")
    suspend fun details(@Path("id") id: Int): Response<StoreModel>
}