package hu.kristof.nagy.hikebookclient.util

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.model.MarkerType
import hu.kristof.nagy.hikebookclient.view.mymap.TextDialogFragment
import hu.kristof.nagy.hikebookclient.viewModel.mymap.RouteViewModel

object SpinnerUtils {
    fun setSpinnerAdapter(context: Context, spinner: Spinner) {
        ArrayAdapter.createFromResource(
            context,
            R.array.markers,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }

    // TODO: try to use sg less error-prone/more flexible instead of ordinal and pos
    fun onItemSelected(
        pos: Int,
        viewModel: RouteViewModel,
        parentFragmentManager: FragmentManager,
        viewLifecycleOwner: LifecycleOwner
    ) {
        when (pos) {
            MarkerType.NEW.ordinal -> {
                viewModel.markerType = MarkerType.NEW
            }
            MarkerType.CASTLE.ordinal -> {
                viewModel.markerType = MarkerType.CASTLE
            }
            MarkerType.LOOKOUT.ordinal -> {
                viewModel.markerType = MarkerType.LOOKOUT
            }
            MarkerType.TEXT.ordinal -> {
                val dialogFragment = TextDialogFragment()
                dialogFragment.show(parentFragmentManager, "text")
                dialogFragment.text.observe(viewLifecycleOwner) {
                    viewModel.markerTitle = it
                }
                viewModel.markerType = MarkerType.TEXT
            }
        }
    }
}