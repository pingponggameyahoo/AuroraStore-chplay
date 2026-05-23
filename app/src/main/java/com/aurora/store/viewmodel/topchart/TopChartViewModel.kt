/*
 * Aurora Store
 *  Copyright (C) 2021, Rahul Kumar Patel <whyorean@gmail.com>
 *
 *  Aurora Store is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Aurora Store is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Aurora Store.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.aurora.store.viewmodel.topchart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurora.gplayapi.data.models.StreamCluster
import com.aurora.gplayapi.helpers.contracts.TopChartsContract
import com.aurora.gplayapi.helpers.web.WebTopChartsHelper
import com.aurora.store.data.model.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

@HiltViewModel
class TopChartViewModel @Inject constructor(
    private val webTopChartsHelper: WebTopChartsHelper
) : ViewModel() {

    // SharedFlow (instead of StateFlow) because StreamCluster overrides equals to compare
    // only id, which is preserved by copy(). StateFlow would conflate paginated updates and
    // break scroll loading. See CategoryStreamViewModel for the same fix.
    private val _state = MutableSharedFlow<ViewState>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val state: SharedFlow<ViewState> = _state.asSharedFlow()

    private val topChartsContract: TopChartsContract
        get() = webTopChartsHelper

    fun getStreamCluster(type: TopChartsContract.Type, chart: TopChartsContract.Chart) {
        getStreamCluster(type.value, chart.value)
    }

    fun getStreamCluster(category: String, chart: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (targetCluster(category, chart).clusterAppList.isNotEmpty()) {
                _state.tryEmit(ViewState.Success(targetCluster(category, chart)))
                return@launch
            }

            _state.tryEmit(ViewState.Loading)

            try {
                val cluster = topChartsContract.getCluster(category, chart)
                updateCluster(category, chart, cluster)
                _state.tryEmit(ViewState.Success(targetCluster(category, chart)))
            } catch (e: Exception) {
                _state.tryEmit(ViewState.Error(e.message))
            }
        }
    }

    fun nextCluster(type: TopChartsContract.Type, chart: TopChartsContract.Chart) {
        nextCluster(type.value, chart.value)
    }

    fun nextCluster(category: String, chart: String) {
        viewModelScope.launch(Dispatchers.IO) {
            supervisorScope {
                try {
                    val target = targetCluster(category, chart)
                    if (target.hasNext()) {
                        val newCluster = topChartsContract.getNextStreamCluster(
                            target.clusterNextPageUrl
                        )

                        updateCluster(category, chart, newCluster)

                        _state.tryEmit(ViewState.Success(targetCluster(category, chart)))
                    }
                } catch (_: Exception) {
                }
            }
        }
    }

    private fun updateCluster(
        type: TopChartsContract.Type,
        chart: TopChartsContract.Chart,
        newCluster: StreamCluster
    ) {
        updateCluster(type.value, chart.value, newCluster)
    }

    private fun updateCluster(
        category: String,
        chart: String,
        newCluster: StreamCluster
    ) {
        val streamCluster = targetCluster(category, chart)
        val mergedCluster = streamCluster.copy(
            clusterNextPageUrl = newCluster.clusterNextPageUrl,
            clusterAppList = streamCluster.clusterAppList + newCluster.clusterAppList
        )

        stringStash
            .getOrPut(category) { mutableMapOf() }
            .set(chart, mergedCluster)
    }

    private val stringStash: MutableMap<String, MutableMap<String, StreamCluster>> = mutableMapOf()

    private fun targetCluster(
        type: TopChartsContract.Type,
        chart: TopChartsContract.Chart
    ): StreamCluster = targetCluster(type.value, chart.value)

    private fun targetCluster(category: String, chart: String): StreamCluster {
        return stringStash
            .getOrPut(category) { mutableMapOf() }
            .getOrPut(chart) { StreamCluster() }
    }
}
