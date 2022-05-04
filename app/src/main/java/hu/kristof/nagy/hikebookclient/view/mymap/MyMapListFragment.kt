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
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.data.network.handleResult
import hu.kristof.nagy.hikebookclient.databinding.FragmentMyMapListBinding
import hu.kristof.nagy.hikebookclient.model.ResponseResult
import hu.kristof.nagy.hikebookclient.model.RouteType
import hu.kristof.nagy.hikebookclient.util.handleOffline
import hu.kristof.nagy.hikebookclient.util.handleOfflineLoad
import hu.kristof.nagy.hikebookclient.util.showGenericErrorOr
import hu.kristof.nagy.hikebookclient.view.help.HelpFragmentDirections
import hu.kristof.nagy.hikebookclient.view.help.HelpRequestType
import hu.kristof.nagy.hikebookclient.viewModel.mymap.MyMapDetailViewModel
import hu.kristof.nagy.hikebookclient.viewModel.mymap.MyMapViewModel

/**
 * A Fragment to list the routes of the logged in user.
 * A list item consists of the route's name, and several buttons:
 * with some, the user can edit, delete, and print the given route,
 * with others, the user can create a group hike, or start a simple hike.
 */
@AndroidEntryPoint
class MyMapListFragment : Fragment() {
    private val myMapViewModel: MyMapViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMyMapListBinding.inflate(inflater, container, false)
            .apply {
                lifecycleOwner = viewLifecycleOwner
            }

        val myMapDetailViewModel: MyMapDetailViewModel by viewModels()

        setupObservers(myMapDetailViewModel)

        binding.switchToMyMapButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_myMapListFragment_to_myMapFragment
            )
        }

        setupList(myMapDetailViewModel, binding)

        setHasOptionsMenu(true)
        return binding.root
    }

    private fun setupObservers(myMapDetailViewModel: MyMapDetailViewModel) {
        myMapDetailViewModel.deleteRes.observe(viewLifecycleOwner) {
            onDeleteResult(myMapDetailViewModel, it)
        }
    }

    private fun onDeleteResult(
        myMapDetailViewModel: MyMapDetailViewModel,
        res: ResponseResult<Boolean>
    ) {
        if (!myMapDetailViewModel.deleteFinished) {
            handleResult(context, res) { deleteRes ->
                showGenericErrorOr(context, deleteRes) {
                    Toast.makeText(context, "A törlés sikeres.", Toast.LENGTH_SHORT).show()
                    handleOfflineLoad(requireContext()) {
                        myMapViewModel.loadRoutesForLoggedInUser() // this refreshes the list and also the routes on the map
                    }
                }
            }
            myMapDetailViewModel.deleteFinished = true
        }
    }

    private fun setupList(
        myMapDetailViewModel: MyMapDetailViewModel,
        binding: FragmentMyMapListBinding
    ) {
        val gotoDetail = { routeName: String ->
            val directions = MyMapListFragmentDirections
                .actionMyMapListFragmentToMyMapDetailFragment(routeName)
            findNavController().navigate(directions)
        }

        val adapter = MyMapListAdapter(
            MyMapClickListener(
                editListener = { routeName ->
                    val action = MyMapListFragmentDirections
                        .actionMyMapListFragmentToRouteEditFragment(RouteType.USER, null, routeName)
                    findNavController().navigate(action)
                },
                deleteListener = { routeName ->
                    handleOffline(requireContext()) {
                        myMapDetailViewModel.deleteRoute(routeName as java.lang.String, requireContext())
                    }
                },
                printListener = { routeName ->
                    gotoDetail(routeName)
                },
                detailNavListener = { routeName ->
                    gotoDetail(routeName)
                },
                hikePlanListener = { routeName ->
                    val directions = MyMapListFragmentDirections
                        .actionMyMapListFragmentToHikePlanDateFragment(routeName)
                    findNavController().navigate(directions)
                },
                groupHikeCreateListener = { routeName ->
                    gotoDetail(routeName)
                }
            )
        )
        binding.myMapRecyclerView.adapter = adapter
        myMapViewModel.routes.observe(viewLifecycleOwner) { res ->
            handleResult(context, res) {
                adapter.submitList(it.toMutableList().map { it.routeName })
            }
        }
    }

    override fun onResume() {
        super.onResume()
        handleOfflineLoad(requireContext()) {
            myMapViewModel.loadRoutesForLoggedInUser()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
     if (item.itemId == R.id.helpMenuItem) {
        val requestType = HelpRequestType.MY_MAP_LIST
        val directions = HelpFragmentDirections.actionGlobalHelpFragment(requestType)
        findNavController().navigate(directions)
        true
    } else {
        super.onOptionsItemSelected(item)
    }
}