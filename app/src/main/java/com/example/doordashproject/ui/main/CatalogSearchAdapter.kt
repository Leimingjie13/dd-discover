package com.example.doordashproject.ui.main

import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.doordashproject.Client
import com.example.doordashproject.databinding.SearchItemBinding
import org.greenrobot.eventbus.EventBus

class CatalogSearchAdapter(var vmClient: Client, var fragment: Fragment?)
    : ListAdapter<Any, StoreViewHolder>(SearchItemCallback(vmClient)),
    View.OnTouchListener {

    var attachedHolders = hashSetOf<BaseViewHolder>()
    var selectedHolders = hashSetOf<BaseViewHolder>()

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        view?.performClick()

        attachedHolders.find {
            it.itemView == view

        }?.also {
            when (event?.actionMasked) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    if (selectedHolders.add(it))
                        EventBus.getDefault().post( SelectEvent(it, true) )
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (selectedHolders.remove(it))
                        EventBus.getDefault().post( SelectEvent(it, false) )
                }
            }
        }
        return false
    }

    override fun getItemCount(): Int {
        return vmClient.liveData.value!!.size
    }

    override fun getItemId(position: Int): Long {
        return vmClient.retrieveId(position).toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int) : StoreViewHolder
    {
        val binding = SearchItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return StoreViewHolder(binding, 2).apply {
            binding.root.interceptTouchListeners.add(this@CatalogSearchAdapter) }
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        holder.id = vmClient.retrieveId(position)
        holder.updateViews(vmClient, fragment!!)
        holder.formatForOrientation(fragment?.resources?.configuration!!.orientation)
    }

    override fun onViewAttachedToWindow(holder: StoreViewHolder) {
        super.onViewAttachedToWindow(holder)
        attachedHolders.add(holder)
        holder.itemView.isEnabled = true
    }

    override fun onViewDetachedFromWindow(holder: StoreViewHolder) {
        super.onViewDetachedFromWindow(holder)
        attachedHolders.remove(holder)
        holder.itemView.isEnabled = false
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        fragment = null
    }
}

class SearchItemCallback(private val vmClient: Client)
    : DiffUtil.ItemCallback<Any>() {

    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        return vmClient.compareModels(oldItem, newItem)
    }
    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        return vmClient.compareData(oldItem, newItem)
    }
}

class SelectEvent(val holder: BaseViewHolder, val isDown: Boolean)