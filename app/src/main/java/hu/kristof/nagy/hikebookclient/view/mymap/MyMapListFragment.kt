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

package hu.kristof.nagy.hikebookclient.view.mymap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.hikebookclient.R
import com.example.hikebookclient.databinding.FragmentMyMapListBinding
import hu.kristof.nagy.hikebookclient.viewModel.mymap.MyMapViewModel

class MyMapListFragment : Fragment() {
    private lateinit var binding: FragmentMyMapListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_my_map_list, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.switchToMyMapButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_myMapListFragment_to_myMapFragment
            )
        }

        val viewModel: MyMapViewModel by activityViewModels()
        val adapter = MyMapListAdapter(MyMapClickListener(
            editListener = { routeName ->
                val route = viewModel.getRoute(routeName)
                val action = MyMapListFragmentDirections
                    .actionMyMapListFragmentToRouteEditFragment(route)
                findNavController().navigate(action)
            },
            deleteListener = { routeName ->
                viewModel.deleteRoute(routeName)
            },
            printListener = { routeName ->
                val route = viewModel.getRoute(routeName)
                val action = MyMapListFragmentDirections
                    .actionMyMapListFragmentToMyMapDetailFragment(route)
                findNavController().navigate(action)
            },
            detailNavListener = { routeName ->
                val route = viewModel.getRoute(routeName)
                val action = MyMapListFragmentDirections
                    .actionMyMapListFragmentToMyMapDetailFragment(route)
                findNavController().navigate(action)
            })
        )
        binding.myMapRecyclerView.adapter = adapter
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.routes.observe(viewLifecycleOwner) {
            adapter.submitList(it.toMutableList())
        }
        viewModel.deleteRes.observe(viewLifecycleOwner) {
            if (!viewModel.deleteFinished) {
                if (it) {
                    Toast.makeText(context, "A törlés sikeres.", Toast.LENGTH_SHORT).show()
                    viewModel.loadRoutesForLoggedInUser() // this refreshes the list and also the routes on the map
                }
                else
                    Toast.makeText(
                        context, resources.getText(R.string.generic_error_msg), Toast.LENGTH_SHORT
                    ).show()
                viewModel.deleteFinished = true
            }
        }
    }
}