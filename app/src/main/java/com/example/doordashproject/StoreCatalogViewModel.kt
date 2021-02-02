package com.example.doordashproject

import androidx.lifecycle.*
import kotlinx.coroutines.*
import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun Client.minutesToClose(id: Int) : String {
    val nextClose: Long
    val format = SimpleDateFormat(Const.TIME_PATTERN, Locale.US)

    try {
        nextClose = format.parse(getById<StoreModel>(id).nextCloseTime)!!.time
    } catch (e: ParseException) {
        return Const.CLOSED
    }

    val currentTime = Calendar.getInstance().time.time

    return when (val mins = (nextClose - Const.TIME_OFFSET - currentTime).toInt() / 1000 / 60) {
        in 1..1440 -> mins.minutesToHours()
        in Int.MIN_VALUE..0 -> Const.CLOSED
        else -> Const.OPEN
    }
}

class StoreCatalogViewModel : ViewModel(), Client {

    override val clientScope = viewModelScope
    override var liveData: MutableLiveData<out MutableList<StoreModel>> = MutableLiveData(mutableListOf())

    var dataMap: MutableMap<Int, StoreModel> = mutableMapOf()

    override fun fillDataMap() {
        dataMap.clear()
        liveData.value?.forEach {
            dataMap[it.id] = it
        }
    }

    override fun <T> compareModels(old: T, new: T) : Boolean {
        return (old as StoreModel).compare(new as StoreModel)
    }

    override fun <T> compareData(old: T, new: T) : Boolean {
        return (old as StoreModel).compareData(new as StoreModel)
    }

    override suspend fun fetchListAsync() : Job {
        return viewModelScope.async {
            liveData.value = StoreRepository.requestList()
        }
    }

    override fun pullDetails(id: Int) : Job? {
        if (dataMap.contains(id)) {
            return viewModelScope.launch {
                try {
                    var newStore: StoreModel = StoreRepository.requestDetails(id)

                    dataMap[id]?.address = newStore.address
                    dataMap[id]?.averageRating = newStore.averageRating
                    dataMap[id]?.phoneNumber = newStore.phoneNumber
                    dataMap[id]?.offersDelivery = newStore.offersDelivery
                    dataMap[id]?.offersPickup = newStore.offersPickup

                    liveData.value = liveData.value
                } catch (e: Exception) {
                    ExceptionHandler
                            .displayDialog("failed to get store details from network: " + e.message)
                }
            }
        } else return null
    }

    override fun retrieveId(index: Int): Int {
        return liveData.value!![index].id
    }

    override fun <T : Any?> getById(id: Int) : T {
        return dataMap[id] as T
    }
}