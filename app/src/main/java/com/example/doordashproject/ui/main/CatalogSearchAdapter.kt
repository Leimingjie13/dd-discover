package com.example.doordashproject.ui.main

import android.annotation.SuppressLint
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.doordashproject.Client
import com.example.doordashproject.databinding.SearchItemBinding
import org.greenrobot.eventbus.EventBus
import kotlin.math.abs

class CatalogSearchAdapter(
    var vmClient: Client,
    var _fragment: Fragment?,
    )
    : ListAdapter<Any, StoreViewHolder>(SearchItemCallback(vmClient)),
    View.OnTouchListener {

    private val fragment get() = _fragment!!
    private var lastX = 0F
    private var xTolerance = 0F

    private var attachedHolders = hashSetOf<BaseViewHolder>()
    var selectedHolders = hashSetOf<BaseViewHolder>()

    // used as intercept touch listener
    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
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
        view?.performClick()
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        holder.id = vmClient.retrieveId(position)
        holder.updateViews(vmClient, fragment)
        holder.formatForOrientation(fragment.resources.configuration!!.orientation)

        (holder.binding as SearchItemBinding).smallDescription
            .setOnTouchListener { view, event ->

                (view as TextView).apply {
                    when (event.actionMasked) {
                        MotionEvent.ACTION_DOWN -> {
                            holder.binding.root
                                .requestDisallowInterceptTouchEvent(true)
                            lastX = event.rawX
                        }
                        MotionEvent.ACTION_MOVE -> {
                            val scrollRange: Int =
                                lineHeight*(lineCount-1) +
                                        firstBaselineToTopHeight +
                                        lastBaselineToBottomHeight -
                                        height

                            // this is only good for views with a smaller size:scroll ratio
                            if (event.y > height || event.y <= 0) {
                                when {
                                    scrollY <= 0 -> holder.binding.root
                                        .requestDisallowInterceptTouchEvent(false)
                                    scrollY >= scrollRange -> holder.binding.root
                                        .requestDisallowInterceptTouchEvent(false)
                                }
                            } else {
                                val dx = event.rawX - lastX
                                xTolerance+=dx

                                if (abs(xTolerance) >= PAGE_SLOP) {
                                    EventBus.getDefault().post( FakeDragEvent(dx, true) )
                                }
                                lastX = event.rawX
                            }
                        }
                        MotionEvent.ACTION_UP -> {
                            holder.binding.root
                                .requestDisallowInterceptTouchEvent(false)
                            EventBus.getDefault().post( FakeDragEvent(0F, false) )
                            xTolerance = 0F
                        }
                    }
                }
                false
        }
    }

    override fun onViewAttachedToWindow(holder: StoreViewHolder) {
        super.onViewAttachedToWindow(holder)
        attachedHolders.add(holder)
        holder.itemView.isEnabled = true
    }

    override fun onViewDetachedFromWindow(holder: StoreViewHolder) {
        super.onViewDetachedFromWindow(holder)
        attachedHolders.remove(holder)
        holder.itemView.setOnTouchListener(null)
        holder.itemView.isEnabled = false
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        _fragment = null
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