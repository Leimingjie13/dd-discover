package com.example.doordashproject.ui.main

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.doordashproject.Client
import com.example.doordashproject.Const
import com.example.doordashproject.databinding.ResultListBinding
import kotlinx.coroutines.CompletionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ListPageHolder(binding: ViewBinding, type: Int)
    : BaseViewHolder(binding, type) {

    fun initialize(vmClient: Client, fragment: Fragment, callback: CompletionHandler)
    : RecyclerView {
        when (type) {
            Const.TYPE_CATALOG -> {
                val catalogAdapter = CatalogSearchAdapter(vmClient, fragment)

                vmClient.liveData.observe(fragment!!, {
                    if ((it as MutableList<*>).isNotEmpty()) {
                        catalogAdapter.submitList(it.toList())
                    }
                })

                vmClient.clientScope.launch(Dispatchers.IO) {
                    val job = vmClient.fetchListAsync()
                    job.join()

                    MainScope().launch {
                        (binding as ResultListBinding).resultList.apply {
                            adapter = catalogAdapter
                            layoutManager =
                                LinearLayoutManager(fragment!!.requireContext())
                            // force live data refresh
                            vmClient.liveData.value = vmClient.liveData.value
                            vmClient.fillDataMap()
                        }
                    }.invokeOnCompletion(callback)
                }
            }
        }
        return (binding as ResultListBinding).resultList
    }

    override fun updateViews(vmClient: Client, fragment: Fragment) {}
}