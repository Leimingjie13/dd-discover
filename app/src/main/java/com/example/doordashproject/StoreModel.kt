package com.example.doordashproject

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Stores() {
    var stores = listOf<StoreModel>()
}

@JsonClass(generateAdapter = false)
data class StoreModel(var id: Int = Const.DEFAULT_ID) {

    @Suppress("unused") // used by Moshi
    constructor() : this(Const.DEFAULT_ID)

    /* fields for search results (discover) */
    var name: String = ""
    var description: String = ""
    var status: StoreStatus = StoreStatus()

    @Json(name = Const.AVERAGE_RATING)
    var averageRating: String = ""

    @Json(name = Const.COVER_IMG_URL)
    var coverImgUrl: String = ""

    @Json(name = Const.DISTANCE_FROM_CONSUMER)
    var distanceFromConsumer: Double = 0.0

    @Json(name = Const.NEXT_CLOSE_TIME)
    var nextCloseTime: String = ""

    @Json(name = Const.NEXT_OPEN_TIME)
    var nextOpenTime: String = ""

    /* fields for store details (details) */
    var address: Address = Address()

    @Json(name = Const.PHONE_NUMBER)
    var phoneNumber: String = ""

    @Json(name = Const.OFFERS_DELIVERY)
    var offersDelivery: Boolean = false

    @Json(name = Const.OFFERS_PICKUP)
    var offersPickup: Boolean = false // maybe StoreStatus pickup_available instead
}

@JsonClass(generateAdapter = true)
class Address() {
    var lat: Float = 0F
    var lng: Float = 0F
    @Json(name = Const.PRINTABLE_ADDRESS)
    var printableAddress: String = ""
}

@JsonClass(generateAdapter = true)
class StoreStatus() {
    @Json(name = Const.ASAP_MINUTES_RANGE)
    var asapMinutesRange: List<Int> = listOf(0, 0)
}

fun StoreModel.compare(otherStore: StoreModel) : Boolean {
    return id == otherStore.id
}

fun StoreModel.compareData(otherStore: StoreModel) : Boolean {
    return this == otherStore
}