package com.example.doordashproject

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

interface Client {
    val liveData: MutableLiveData<out MutableList<*>>
    val clientScope: CoroutineScope

    fun fillDataMap()

    fun <T> compareModels(old: T, new: T) : Boolean
    fun <T> compareData(old: T, new: T) : Boolean
    fun retrieveId(index: Int) : Int
    fun <T : Any?> getById(id: Int) : T

    fun pullDetails(id: Int) : Job?
    suspend fun fetchListAsync() : Job
}