package hu.kristof.nagy.hikebookclient.view.groups

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.data.network.handleResult
import hu.kristof.nagy.hikebookclient.databinding.AddFromMyMapDialogBinding
import hu.kristof.nagy.hikebookclient.model.ServerResponseResult
import hu.kristof.nagy.hikebookclient.model.routes.Route
import hu.kristof.nagy.hikebookclient.model.routes.UserRoute
import hu.kristof.nagy.hikebookclient.util.checkAndHandleRouteLoad
import hu.kristof.nagy.hikebookclient.util.handleOfflineLoad
import hu.kristof.nagy.hikebookclient.viewModel.groups.AddFromMyMapViewModel

/**
 * A Fragment to add a route from my map to group map.
 * It displays the list of routes available on the user's map.
 */
@AndroidEntryPoint
class AddFromMyMapDialogFragment : DialogFragment(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: AddFromMyMapDialogBinding
    private val viewModel: AddFromMyMapViewModel by viewModels()

    private var _route = MutableLiveData<Route>()
    val route: LiveData<Route>
        get() = _route

    private var chosenPos = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            binding = AddFromMyMapDialogBinding.inflate(inflater,null, false)

            binding.addFromMyMapDialogSpinner.onItemSelectedListener = this

            setupLoad()

            builder.setView(binding.root)
                .setPositiveButton("OK") { _, _ ->
                    if (checkAndHandleRouteLoad(_route.value!!)) {
                        if (chosenPos == 0) {
                            Toast.makeText(
                                requireContext(), "Kérem, hogy válasszon útvonalat!", Toast.LENGTH_LONG
                            ).show()
                        } else {
                            // we subtract 1 because we have added a filler string to the start of objects
                            _route.value = viewModel.routes.value!!.successResult!![chosenPos - 1]
                        }
                    }
                }
                .setNegativeButton("Mégse") { _, _ ->
                    dialog?.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun setupLoad() {
        // TODO: when testing this, place big emphasis on lifecycle
        //       related concerns as this observer is different from usual,
        //       namely, it uses the parent fragment's viewLifecycleOwner
        //       instead of its own
        //       OR try to make it work with its own viewLifecycleOwner
        binding.lifecycleOwner = parentFragment?.viewLifecycleOwner
        viewModel.routes.observe(requireParentFragment().viewLifecycleOwner, observer)
        handleOfflineLoad(requireContext()) {
            viewModel.loadRoutesForLoggedInUser()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        viewModel.routes.removeObserver(observer)
    }

    private val observer = { res: ServerResponseResult<List<UserRoute>> ->
        handleResult(context, res) { routes ->
            val routeNames = routes.map { it.routeName }
            val objects = mutableListOf("Válassz útvonalat")
            objects.addAll(routeNames)
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                objects
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.addFromMyMapDialogSpinner.adapter = adapter
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        chosenPos = pos
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}