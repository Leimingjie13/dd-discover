package com.example.doordashproject.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.doordashproject.Const
import com.example.doordashproject.databinding.StoreDiscoveryFragmentBinding
import com.example.doordashproject.R
import com.example.doordashproject.StoreCatalogViewModel

class StoreDiscoveryFragment : Fragment(R.layout.store_discovery_fragment) {

    lateinit var viewModel: StoreCatalogViewModel
    lateinit var viewPagerAdapter: ViewPagerAdapter

    private var binding: StoreDiscoveryFragmentBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(StoreCatalogViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View {
        binding =
            StoreDiscoveryFragmentBinding.inflate(inflater, container, false)
        return binding!!.discoverPager
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPagerAdapter = ViewPagerAdapter(viewModel, this,
            Const.TYPE_CATALOG,
            Const.TYPE_SINGLEVIEW,
        ).apply {
            lastState = savedInstanceState
        }

        binding!!.also {
            viewPagerAdapter.apply {
                pager = it.discoverPager
                this.stateRestorationPolicy =
                    RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

                it.root.adapter = this
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(Const.SAVESTATE, viewPagerAdapter.obtainState())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}