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
// https://github.com/google-developer-training/android-kotlin-fundamentals-apps/tree/master/RecyclerViewClickHandler

package hu.kristof.nagy.hikebookclient.view.mymap

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hu.kristof.nagy.hikebookclient.databinding.MyMapListItemBinding

class MyMapListAdapter(private val clickListener: MyMapClickListener)
    : ListAdapter<String, MyMapListAdapter.ViewHolder>(MyMapListDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, clickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val routeName = getItem(position)
        holder.bind(routeName)
    }

    class ViewHolder private constructor(
        private val binding : MyMapListItemBinding,
        private val clickListener: MyMapClickListener
        ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(routeName: String) {
            binding.routeName = routeName
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup, clickListener: MyMapClickListener): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = MyMapListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, clickListener)
            }
        }
    }
}

class MyMapListDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}

class MyMapClickListener(
    private val editListener: (routeName: String) -> Unit,
    private val deleteListener: (routeName: String) -> Unit,
    private val printListener: (routeName: String) -> Unit,
    private val detailNavListener: (routeName: String) -> Unit,
    private val hikePlanListener: (routeName: String) -> Unit,
    private val groupHikeCreateListener: (routeName: String) -> Unit
) {
    fun onEdit(routeName: String) = editListener(routeName)
    fun onDelete(routeName: String) = deleteListener(routeName)
    fun onPrint(routeName: String) = printListener(routeName)
    fun onDetailNav(routeName: String) = detailNavListener(routeName)
    fun onHikePlan(routeName: String) = hikePlanListener(routeName)
    fun onGroupHikeCreate(routeName: String) = groupHikeCreateListener(routeName)
}