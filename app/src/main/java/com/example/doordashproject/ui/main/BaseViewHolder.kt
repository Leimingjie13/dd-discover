package com.example.doordashproject.ui.main

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.doordashproject.Client
import com.example.doordashproject.Const

abstract class BaseViewHolder
    : RecyclerView.ViewHolder {

    val binding: ViewBinding
    val type: Int

    constructor(binding: ViewBinding, type: Int = -1) : super(binding.root) {
        this.binding = binding
        this.type = type
        this.selectedHolders = mutableListOf()
    }

    open var id: Int = Const.DEFAULT_ID
    open var selectedHolders: MutableList<BaseViewHolder>
    abstract fun updateViews(vmClient: Client, fragment: Fragment)
    open fun formatForOrientation(orientation: Int) {}
}