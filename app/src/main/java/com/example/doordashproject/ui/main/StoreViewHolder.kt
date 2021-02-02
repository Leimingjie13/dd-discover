package com.example.doordashproject.ui.main

import android.content.res.Configuration
import android.graphics.Paint
import android.text.method.ScrollingMovementMethod
import android.text.SpannableStringBuilder
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.doordashproject.*
import com.example.doordashproject.databinding.ItemDetailsBinding
import com.example.doordashproject.databinding.SearchItemBinding

class StoreViewHolder(binding: ViewBinding, type: Int) : BaseViewHolder(binding, type) {

    override var id = Const.DEFAULT_ID

    override fun updateViews(vmClient: Client, fragment: Fragment) {
        when (binding) {
            is SearchItemBinding -> {
                val data = (vmClient as StoreCatalogViewModel).dataMap

                binding.smallName.text = SpannableStringBuilder(data[id]?.name)
                binding.smallDescription.text = data[id]?.description
                binding.smallReadyTime.text =
                        data[id]?.status?.asapMinutesRange?.get(0).toString()

                Glide.with(fragment)
                    .load(data[id]?.coverImgUrl)
                    .into(binding.smallCoverImage)
            }

            is ItemDetailsBinding -> {
                val data = (vmClient as StoreCatalogViewModel).dataMap

                binding.address.text = data[id]?.address?.printableAddress

                binding.averageRating.text = data[id]?.averageRating.toString()

                binding.description.text = SpannableStringBuilder(data[id]?.description)

                binding.distance.text = String.format(
                        fragment.getString(R.string.distance),
                        data[id]?.distanceFromConsumer?.toString(2))

                binding.name.text = SpannableStringBuilder(data[id]?.name)
                if (binding.phoneNumber.text.length >= 11) {
                    binding.phoneNumber.text = String.format("%1s-%2s-%3s",
                            data[id]?.phoneNumber?.substring(1..3),
                            data[id]?.phoneNumber?.substring(4..6),
                            data[id]?.phoneNumber?.substring(7..10),
                    )
                }

                binding.readyTime.text = String.format(
                        fragment.getString(R.string.ready_time),
                        data[id]?.status?.asapMinutesRange?.get(0))

                binding.timeToClose.text = vmClient.minutesToClose(id)

                    if (!data[id]!!.offersDelivery) {
                        binding.delivery.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    }
                    if (!data[id]!!.offersPickup) {
                        binding.takeout.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    }

                    Glide.with(fragment)
                        .load(data[id]?.coverImgUrl)
                        .into(binding.coverImage)
            }
            else -> ExceptionHandler.displaySnackbar("can't bind children, unknown view type")
        }
    }

    // for now, format the Views after updating them
    override fun formatForOrientation(orientation: Int) {
        when (binding) {
            is SearchItemBinding -> {
                binding.smallDescription.movementMethod = ScrollingMovementMethod.getInstance()

                if (orientation == Configuration.ORIENTATION_LANDSCAPE)
                    binding.smallName.textSize = Const.LANDSCAPE_TEXT_SIZE
            }
            is ItemDetailsBinding -> {
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    // I had intended to put each word in the description on a separate line
                    //  and line them up to the right
                }
            }
            else -> ExceptionHandler.displayDialog("can't configure view, unknown type")
        }
    }
}