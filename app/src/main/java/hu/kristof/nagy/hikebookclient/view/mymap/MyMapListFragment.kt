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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.data.network.handleResult
import hu.kristof.nagy.hikebookclient.databinding.FragmentMyMapListBinding
import hu.kristof.nagy.hikebookclient.model.RouteType
import hu.kristof.nagy.hikebookclient.util.handleOffline
import hu.kristof.nagy.hikebookclient.util.showGenericErrorOr
import hu.kristof.nagy.hikebookclient.view.help.HelpFragmentDirections
import hu.kristof.nagy.hikebookclient.view.help.HelpRequestType
import hu.kristof.nagy.hikebookclient.viewModel.mymap.MyMapDetailViewModel
import hu.kristof.nagy.hikebookclient.viewModel.mymap.MyMapViewModel

/**
 * A Fragment to list the routes of the logged in user.
 */
@AndroidEntryPoint
class MyMapListFragment : Fragment() {
    private lateinit var binding: FragmentMyMapListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_my_map_list, container, false
        )
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.switchToMyMapButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_myMapListFragment_to_myMapFragment
            )
        }

        val myMapViewModel: MyMapViewModel by activityViewModels()
        val myMapDetailViewModel: MyMapDetailViewModel by viewModels()

        setupRecyclerView(myMapDetailViewModel, myMapViewModel)

        binding.lifecycleOwner = viewLifecycleOwner
        myMapDetailViewModel.deleteRes.observe(viewLifecycleOwner) {
            onDeleteResult(myMapViewModel, myMapDetailViewModel, it)
        }
        myMapDetailViewModel.groupHikeCreateRes.observe(viewLifecycleOwner) { res ->
            onGroupHikeCreateResult(myMapDetailViewModel, res)
        }
    }

    private fun onGroupHikeCreateResult(
        myMapDetailViewModel: MyMapDetailViewModel,
        res: Result<Boolean>
    ) {
        if (!myMapDetailViewModel.groupHikeCreationFinished) {
            handleResult(context, res) { groupHikeCreateRes ->
                showGenericErrorOr(
                    context,
                    groupHikeCreateRes,
                    "A csoportos túra létrehozása sikeres!"
                )
            }
            myMapDetailViewModel.groupHikeCreationFinished = true
        }
    }

    private fun onDeleteResult(
        myMapViewModel: MyMapViewModel,
        myMapDetailViewModel: MyMapDetailViewModel,
        res: Result<Boolean>
    ) {
        if (!myMapDetailViewModel.deleteFinished) {
            handleResult(context, res) { deleteRes ->
                showGenericErrorOr(context, deleteRes) {
                    Toast.makeText(context, "A törlés sikeres.", Toast.LENGTH_SHORT).show()
                    myMapViewModel.loadRoutesForLoggedInUser() // this refreshes the list and also the routes on the map
                }
            }
            myMapDetailViewModel.deleteFinished = true
        }
    }

    private fun setupRecyclerView(
        myMapDetailViewModel: MyMapDetailViewModel,
        myMapViewModel: MyMapViewModel
    ) {
        val adapter = MyMapListAdapter(
            MyMapClickListener(
                editListener = { routeName ->
                    val action = MyMapListFragmentDirections
                        .actionMyMapListFragmentToRouteEditFragment(RouteType.USER, null, routeName)
                    findNavController().navigate(action)
                },
                deleteListener = { routeName ->
                    handleOffline(requireContext()) {
                        myMapDetailViewModel.deleteRoute(routeName)
                    }
                },
                printListener = { routeName ->
                    val directions = MyMapListFragmentDirections
                        .actionMyMapListFragmentToMyMapDetailFragment(routeName)
                    findNavController().navigate(directions)
                },
                detailNavListener = { routeName ->
                    val directions = MyMapListFragmentDirections
                        .actionMyMapListFragmentToMyMapDetailFragment(routeName)
                    findNavController().navigate(directions)
                },
                hikePlanListener = { routeName ->
                    val directions = MyMapListFragmentDirections
                        .actionMyMapListFragmentToHikePlanDateFragment(routeName)
                    findNavController().navigate(directions)
                },
                groupHikeCreateListener = { routeName ->
                    val directions = MyMapListFragmentDirections
                        .actionMyMapListFragmentToMyMapDetailFragment(routeName)
                    findNavController().navigate(directions)
                }
            )
        )
        binding.myMapRecyclerView.adapter = adapter
        binding.lifecycleOwner = viewLifecycleOwner
        myMapViewModel.routes.observe(viewLifecycleOwner) { res ->
            handleResult(context, res) {
                adapter.submitList(it.toMutableList().map { it.routeName })
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
     if (item.itemId == R.id.helpMenuItem) {
        val requestType = HelpRequestType.MY_MAP_LIST
        val action = HelpFragmentDirections.actionGlobalHelpFragment(requestType)
        findNavController().navigate(action)
        true
    } else {
        super.onOptionsItemSelected(item)
    }
}