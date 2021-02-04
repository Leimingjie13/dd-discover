package com.example.doordashproject.ui.main

import android.content.res.Configuration
import android.graphics.Paint
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.doordashproject.*
import com.example.doordashproject.databinding.ItemDetailsBinding
import com.example.doordashproject.databinding.SearchItemBinding

class StoreViewHolder(binding: ViewBinding, type: Int) : BaseViewHolder(binding, type) {

    override var id = Const.DEFAULT_ID

    override fun updateViews(vmClient: Client, fragment: Fragment) {
        val data: MutableMap<Int, *>

        when {
            binding is SearchItemBinding
                    && vmClient is StoreCatalogViewModel -> {
                data = vmClient.dataMap

                data[id]?.run {
                    binding.smallName
                        .apply { setText(name, TextView.BufferType.EDITABLE) }
                    binding.smallDescription.text = description
                    binding.smallReadyTime.text =
                        status.asapMinutesRange[0].toString()

                    Glide.with(fragment)
                        .load(coverImgUrl)
                        .into(binding.smallCoverImage)
                }
            }

            binding is ItemDetailsBinding
                    && vmClient is StoreCatalogViewModel -> {
                data = vmClient.dataMap

                data[id]?.run {
                    binding.address.text = address.printableAddress

                    binding.averageRating.text = averageRating

                    binding.description
                        .apply { setText(description, TextView.BufferType.EDITABLE) }

                    binding.distance.text = String.format(
                        fragment.getString(R.string.distance),
                        distanceFromConsumer.toString(2)
                    )

                    binding.name
                        .apply { setText(name, TextView.BufferType.EDITABLE) }

                    if (phoneNumber.length >= 11) {
                        binding.phoneNumber.text = String.format("%1s-%2s-%3s",
                            phoneNumber.substring(1..3),
                            phoneNumber.substring(4..6),
                            phoneNumber.substring(7..10),
                        )
                    }

                    binding.readyTime.text = String.format(
                        fragment.getString(R.string.ready_time),
                        status.asapMinutesRange[0]
                    )


                    binding.timeToClose.text = vmClient.minutesToClose(id)

                    binding.delivery.paintFlags.also {
                        if (offersDelivery) {
                            binding.delivery.paintFlags = it and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                        } else {
                            binding.delivery.paintFlags = it or Paint.STRIKE_THRU_TEXT_FLAG
                        }
                    }

                    binding.takeout.paintFlags.also {
                        if (offersPickup) {
                            binding.takeout.paintFlags = it and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                        } else {
                            binding.takeout.paintFlags = it or Paint.STRIKE_THRU_TEXT_FLAG
                        }
                    }

                    Glide.with(fragment)
                        .load(coverImgUrl)
                        .into(binding.coverImage)
                }
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