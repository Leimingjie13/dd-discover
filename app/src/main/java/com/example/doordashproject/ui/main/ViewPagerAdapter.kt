package com.example.doordashproject.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.doordashproject.Client
import com.example.doordashproject.Const
import com.example.doordashproject.databinding.ItemDetailsBinding
import com.example.doordashproject.databinding.ResultListBinding
import com.example.doordashproject.ExceptionHandler
import com.example.doordashproject.MainActivity.BackEvent
import java.lang.IllegalArgumentException
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

const val PAGE_SLOP = 108

class ViewPagerAdapter(
    var vmClient: Client,
    var _fragment: Fragment?,
    vararg var outline: Int)
    : RecyclerView.Adapter<BaseViewHolder>(), LifecycleObserver {

    private val selectedHolders = mutableListOf<BaseViewHolder>()
    private val fragment get() = _fragment!!

    var isScrolling = false
    var pages = mutableListOf<BaseViewHolder>()
    var lastState: Bundle? = null

    lateinit var pager: ViewPager2

    var pageChangeCallback: ViewPager2.OnPageChangeCallback =
            object : ViewPager2.OnPageChangeCallback() {

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)

                    if (state == ViewPager2.SCROLL_STATE_SETTLING) {
                        pager.apply {
                            endFakeDrag()
                            isUserInputEnabled = true
                        }
                        isScrolling = false
                    }
                }
                override fun onPageScrolled(
                        position: Int,
                        positionOffset: Float,
                        positionOffsetPixels: Int) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                    isScrolling = true
                }
            }

    init { EventBus.getDefault().register(this) }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,)
    : BaseViewHolder {
        val inflater = LayoutInflater.from(fragment.requireContext())

        return when (viewType) {
            Const.TYPE_CATALOG -> {
                pager.isUserInputEnabled = false
                pager.registerOnPageChangeCallback(pageChangeCallback)

                val binding = ResultListBinding
                        .inflate(inflater, parent, false)
                        .apply {
                            resultList.setHasFixedSize(true)
                        }

                ListPageHolder(binding, Const.TYPE_CATALOG)
                        .apply {
                            initialize(vmClient, fragment) {
                                pager.isUserInputEnabled = true
                            }
                                .layoutManager?.onRestoreInstanceState(
                                    lastState?.getBundle(Const.SAVED_LAYOUT))

                            lastState = null
                            pages.add(0, this)
                        }
            }

            Const.TYPE_SINGLEVIEW -> {
                StoreViewHolder(
                    ItemDetailsBinding.inflate(inflater, parent, false),
                Const.TYPE_SINGLEVIEW)
                    .apply {
                        pages.add(this)
                    }
            }

            else -> throw IllegalArgumentException("Pager doesn't recognize type")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        lastState?.run {
            if (position == 1) {
                this.getBundle(Const.SAVESTATE)?.getInt(Const.SAVED_ID)?.let {
                    val job = vmClient.pullDetails(it)
                    MainScope().launch {
                        job?.join()
                        holder.id = it
                        holder.updateViews(vmClient, fragment)
                        holder.formatForOrientation(
                            fragment.resources.configuration!!.orientation)
                    }
                }
            }
        }
        lastState = null
    }

    override fun onViewAttachedToWindow(holder: BaseViewHolder) {
        super.onViewAttachedToWindow(holder)

        when (holder) {
            pages.getOrNull(1) -> {
                try {
                    selectedHolders.last().id.also {
                        if (it != holder.id) {
                            val job = vmClient.pullDetails(it)
                            MainScope().launch {
                                job?.join()
                                holder.id = it
                                holder.updateViews(vmClient, fragment)
                                holder.formatForOrientation(
                                    fragment.resources.configuration!!.orientation)
                            }
                        }
                    }
                } catch (e: Exception) {
                    when (e) {
                       is NoSuchElementException ->
                           ExceptionHandler.snackbar(
                               "No selected holders in view pager adapter: " + e.message)
                    }
                }
            }
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        pager.unregisterOnPageChangeCallback(pageChangeCallback)
        _fragment = null
    }

    override fun getItemCount(): Int {
        return outline.size
    }

    override fun getItemViewType(position: Int): Int {
        return outline[position]
    }

    fun obtainState() : Bundle? {
        val index = pager.currentItem
        return when(outline[index]) {
            Const.TYPE_CATALOG -> {
                Bundle().apply {
                    putParcelable(
                        Const.SAVED_LAYOUT,
                    ((pages.find { it.type == Const.TYPE_CATALOG }
                        ?.binding?.root) as RecyclerView)
                        .layoutManager?.onSaveInstanceState())
                }
            }
            Const.TYPE_SINGLEVIEW -> Bundle()
                .apply {
                    pages.find { it.type == Const.TYPE_SINGLEVIEW }?.id?.let {
                        putInt(Const.SAVED_ID, it)
                    }}
            else -> null
        }
    }

    @Subscribe
    fun onBackPressed(event: BackEvent) {
        if (pager.currentItem != 0) {
            pager.isUserInputEnabled = false
            pager.setCurrentItem(0, true)
        } else {
            event.ignore()
        }
    }

    @Subscribe
    fun onHolderSelected(event: SelectEvent) {
        if (event.isDown) {
            selectedHolders.add(event.holder)
        } else if (!isScrolling){
            selectedHolders.remove(event.holder)
        }
    }

    @Subscribe
    fun onFakeDrag(event: FakeDragEvent) {
        pager.apply {
            if (!isFakeDragging && event.drag) {
                beginFakeDrag()
            } else if (!event.drag) {
                endFakeDrag()
            }
            fakeDragBy(event.f)
        }
    }
}

class SelectEvent(val holder: BaseViewHolder, val isDown: Boolean)
class FakeDragEvent(val f: Float, val drag: Boolean)