/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// based on:
// https://github.com/google-developer-training/android-kotlin-fundamentals-apps/tree/master/RecyclerViewFundamentals
// https://github.com/android/sunflower

package hu.kristof.nagy.hikebookclient.view.mymap

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hikebookclient.R
import com.example.hikebookclient.databinding.MyMapListItemBinding
import hu.kristof.nagy.hikebookclient.model.Route
import hu.kristof.nagy.hikebookclient.viewModel.mymap.MyMapViewModel

class MyMapListAdapter(private val viewModel: MyMapViewModel)
    : ListAdapter<Route, MyMapListAdapter.ViewHolder>(MyMapListDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, viewModel)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class ViewHolder private constructor(
        val binding: MyMapListItemBinding,
        val viewModel: MyMapViewModel
        ) :
        RecyclerView.ViewHolder(binding.root) {
        val tv: TextView = binding.myMapListItemRouteName

        init {
            binding.myMapListItemEditImageButton.setOnClickListener {
                it.findNavController().navigate(
                    R.id.action_myMapListFragment_to_routeEditFragment
                )
            }
            binding.myMapListItemDeleteImageButton.setOnClickListener {
                viewModel.deleteRoute(tv.text.toString())
            }
        }

        fun bind(route: Route) {
            tv.text = route.routeName
        }

        companion object {
            fun from(parent: ViewGroup, viewModel: MyMapViewModel) : ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = MyMapListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, viewModel)
            }
        }
    }
}

class MyMapListDiffCallback : DiffUtil.ItemCallback<Route>() {
    override fun areItemsTheSame(oldItem: Route, newItem: Route): Boolean {
        return oldItem.routeName == newItem.routeName
    }

    override fun areContentsTheSame(oldItem: Route, newItem: Route): Boolean {
        return oldItem == newItem
    }

}