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
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.data.network.handleResult
import hu.kristof.nagy.hikebookclient.databinding.FragmentMyMapListBinding
import hu.kristof.nagy.hikebookclient.model.RouteType
import hu.kristof.nagy.hikebookclient.util.showGenericErrorOr
import hu.kristof.nagy.hikebookclient.view.help.HelpFragmentDirections
import hu.kristof.nagy.hikebookclient.view.help.HelpRequestType
import hu.kristof.nagy.hikebookclient.view.hike.TimePickerFragment
import hu.kristof.nagy.hikebookclient.view.routes.TextDialogFragment
import hu.kristof.nagy.hikebookclient.viewModel.mymap.MyMapListViewModel
import hu.kristof.nagy.hikebookclient.viewModel.mymap.MyMapViewModel
import java.util.*

/**
 * A Fragment to list the routes of the logged in user.
 */
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
        val myMapListViewModel: MyMapListViewModel by viewModels()
        setupAdapter(myMapListViewModel, myMapViewModel)
        binding.lifecycleOwner = viewLifecycleOwner
        myMapListViewModel.deleteRes.observe(viewLifecycleOwner) {
            onDeleteResult(myMapViewModel, myMapListViewModel, it)
        }
        myMapListViewModel.groupHikeCreateRes.observe(viewLifecycleOwner) { res ->
            if (!myMapListViewModel.groupHikeCreationFinished) {
                handleResult(context, res) { groupHikeCreateRes ->
                    showGenericErrorOr(context, groupHikeCreateRes, "A csoportos túra létrehozása sikeres!")
                }
                myMapListViewModel.groupHikeCreationFinished = true
            }
        }
    }

    private fun onDeleteResult(
        myMapViewModel: MyMapViewModel,
        myMapListViewModel: MyMapListViewModel,
        res: Result<Boolean>
    ) {
        if (!myMapListViewModel.deleteFinished) {
            handleResult(context, res) { deleteRes ->
                showGenericErrorOr(context, deleteRes) {
                    Toast.makeText(context, "A törlés sikeres.", Toast.LENGTH_SHORT).show()
                    myMapViewModel.loadRoutesForLoggedInUser() // this refreshes the list and also the routes on the map
                }
            }
            myMapListViewModel.deleteFinished = true
        }
    }

    private fun setupAdapter(
        myMapListViewModel: MyMapListViewModel,
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
                    myMapListViewModel.deleteRoute(routeName)
                },
                printListener = { routeName ->
                    val action = MyMapListFragmentDirections
                        .actionMyMapListFragmentToMyMapDetailFragment(routeName)
                    findNavController().navigate(action)
                },
                detailNavListener = { routeName ->
                    val action = MyMapListFragmentDirections
                        .actionMyMapListFragmentToMyMapDetailFragment(routeName)
                    findNavController().navigate(action)
                },
                hikePlanListener = { routeName ->
                    val directions = MyMapListFragmentDirections
                        .actionMyMapListFragmentToHikePlanDateFragment(routeName)
                    findNavController().navigate(directions)
                },
                groupHikeCreateListener = { routeName ->
                    val dateTime = Calendar.getInstance().apply { clear() }

                    // note to reader: read this block in reverse order
                    // as that is the way in which the dialogs will be shown to the user
                    val groupHikeCreateDialog = TextDialogFragment.instanceOf(
                        R.string.group_hike_create_text, R.string.groups_create_dialog_hint_text
                    )
                    groupHikeCreateDialog.text.observe(viewLifecycleOwner) { name ->
                        try {
                            myMapListViewModel.createGroupHike(dateTime, routeName, name)
                        } catch (e: IllegalArgumentException) {
                            Toast.makeText(context, e.message!!, Toast.LENGTH_LONG).show()
                        }
                    }

                    val timeDialog = TimePickerFragment()
                    timeDialog.timeRes.observe(viewLifecycleOwner) { timeRes ->
                        dateTime.set(Calendar.HOUR_OF_DAY, timeRes.get(Calendar.HOUR_OF_DAY))
                        dateTime.set(Calendar.MINUTE, timeRes.get(Calendar.MINUTE))

                        groupHikeCreateDialog.show(parentFragmentManager, "group hike name")
                    }

                    val dateDialog = DatePickerFragment()
                    binding.lifecycleOwner = viewLifecycleOwner
                    dateDialog.dateRes.observe(viewLifecycleOwner) { dateRes ->
                        dateTime.set(Calendar.YEAR, dateRes.get(Calendar.YEAR))
                        dateTime.set(Calendar.MONTH, dateRes.get(Calendar.MONTH))
                        dateTime.set(Calendar.DAY_OF_MONTH, dateRes.get(Calendar.DAY_OF_MONTH))

                        timeDialog.show(parentFragmentManager, "group hike time")
                    }

                    dateDialog.show(parentFragmentManager, "group hike date")
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