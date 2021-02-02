package com.example.doordashproject

import androidx.lifecycle.MutableLiveData
import com.example.doordashproject.ui.main.SearchItemCallback
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class SampleDoorDashTests {

    var viewModel: StoreCatalogViewModel? = null
    var store: StoreModel? = null
    var store2: StoreModel? = null
    var callback: SearchItemCallback? = null

    val json =
        """
            {
            	"stores": [
            		{
            			"name" : "China Renaissance"
            			"id" : 1954
            		}
            	]
            }
        """.trimIndent()

    @Before
    fun initialize() {
        store = StoreModel(500)
        store!!.nextCloseTime = "2020-12-31'T'12:00:00'Z'"

        store2 = StoreModel(100)
        store2!!.nextCloseTime = "2021-0206'T'06:00:30'Z'"

        viewModel = StoreCatalogViewModel()
        viewModel!!.liveData = MutableLiveData(mutableListOf(store!!, store2!!))
        viewModel!!.fillDataMap()

        callback = SearchItemCallback(viewModel!!)
    }

    @After
    fun shutdown() {
        callback = null
        store = null
        store2 = null
        viewModel = null
    }

    @Test
    fun util_minutesToHours_lessThan60Mins() {
        assertEquals("57 minutes", 57.minutesToHours())
    }

    @Test
    fun util_minutesToHours_hoursAndMinutes() {
        assertEquals("4 H, 20 M", 260.minutesToHours())
    }

    @Test
    fun storeModelCatalogViewModel_minutesToClose_closedForPastCloseTime() {
        var actual = viewModel!!.minutesToClose(500)
        assertEquals("Closed", actual)
    }

    @Test
    fun storeModelCatalogViewModel_minutesToClose_closeOnBadFormat() {
        var actual = viewModel!!.minutesToClose(100)
        assertEquals("Closed", actual)
    }

    @Test
    fun searchItemCallback_areContentsTheSame_trueForSame() {
        assertEquals(true, store!!.let { callback!!.areContentsTheSame(it, it) })
    }
}