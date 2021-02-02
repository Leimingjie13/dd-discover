package com.example.doordashproject

import java.text.DecimalFormat
import kotlin.random.Random

fun createRandomStrings(length: Int, number: Int) : List<String> {
    val set = mutableSetOf<String>()

    while (set.size <= number) {
        set.add(
            (1..length).map {
                Random.Default.nextInt(33, 126).toChar()
            }.joinToString(""))
    }
    return set.toList()
}

fun Double.toString(places: Int) : String {
    return DecimalFormat().let {
        it.maximumFractionDigits = places
        it.format(this)
    }
}

fun Int.minutesToHours() : String {
    return if (this >= 60) {
        String.format("%1d H, %2d M", this/60, this%60)
    } else String.format("%d minutes", this)
}

object Const {

    const val DEFAULT_ID = -1

    /* For ViewPager2 */
    const val TYPE_CATALOG = 0
    const val TYPE_SINGLEVIEW = 1

    const val PORTRAIT_TEXT_SIZE = 20F
    const val LANDSCAPE_TEXT_SIZE = 30F

    const val TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    // rough offset to get closer to correct time from API values
    const val TIME_OFFSET = 28_750_000

    const val BASE_URL = "https://api.doordash.com"
    const val NA = "n/a"
    const val UNKNOWN = "unknown"
    const val OPEN = "Open"
    const val CLOSED = "Closed"

    const val ADDRESS = "address"
    const val ASAP_MINUTES_RANGE = "asap_minutes_range"
    const val AVERAGE_RATING = "average_rating"
    const val COVER_IMG_URL = "cover_img_url"
    const val DESCRIPTION = "description"
    const val DISTANCE_FROM_CONSUMER = "distance_from_consumer"
    const val ID = "id"
    const val NAME = "name"
    const val NEXT_CLOSE_TIME = "next_close_time"
    const val NEXT_OPEN_TIME = "next_open_time"
    const val STATUS = "status"
    const val OFFERS_DELIVERY = "offers_delivery"
    const val OFFERS_PICKUP = "offers_pickup"
    const val PHONE_NUMBER = "phone_number"
    const val PRINTABLE_ADDRESS = "printable_address"

    const val SAVESTATE = "master_saved_state"
    const val SAVED_LAYOUT = "result_list_layout_info"
    const val SAVED_ID = "details_page_id"
}

object Json {

    var moshiMap = mutableListOf(
        // 0
        Const.ADDRESS,
        Const.AVERAGE_RATING,
        Const.COVER_IMG_URL,
        Const.DESCRIPTION,
        Const.DISTANCE_FROM_CONSUMER,
        // 5
        Const.ID,
        Const.NAME,
        Const.NEXT_CLOSE_TIME,
        Const.NEXT_OPEN_TIME,
        Const.OFFERS_DELIVERY,
        // 10
        Const.OFFERS_PICKUP,
        Const.PHONE_NUMBER,
        Const.STATUS,
    )

    val randomNames = createRandomStrings(8, moshiMap.size)
    val discoverOptions = listOf(2, 3, 4, 5, 6, 7, 8, 12)
    val detailsOptions = listOf(0, 1, 9, 10, 11)
}

/*// These overloads were originally for Moshi adapter reflective resolution
operator fun String.div(default: String) : Pair<String, String> {
    return Pair(this, default)
}
operator fun SortedMap<String, Pair<String, String>>.get(index: Int)
: Pair<String, String> {
    return this.toList()[index].second
}*/