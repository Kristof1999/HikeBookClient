package hu.kristof.nagy.hikebookclient.util

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.view.mymap.MarkerType
import hu.kristof.nagy.hikebookclient.view.routes.TextDialogFragment
import hu.kristof.nagy.hikebookclient.viewModel.routes.RouteViewModel

object SpinnerUtils {

    fun setTransportSpinnerAdapter(context: Context, spinner: Spinner) {
        setSpinnerAdatper(context, spinner, R.array.transport_types)
    }

    fun setMarkerSpinnerAdapter(context: Context, spinner: Spinner) {
        setSpinnerAdatper(context, spinner, R.array.markers)
    }

    private fun setSpinnerAdatper(context: Context, spinner: Spinner, arrayId: Int) {
        ArrayAdapter.createFromResource(
            context,
            arrayId,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }

    fun onMarkerItemSelected(
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
                // TODO: after text marker is placed, switch back to new marker
                val dialogFragment = TextDialogFragment.instanceOf(
                    R.string.marker_text_dialog_text, R.string.marker_text_dialog_hint
                )
                dialogFragment.text.observe(viewLifecycleOwner) {
                    viewModel.markerTitle = it
                }
                dialogFragment.show(parentFragmentManager, "text")
                viewModel.markerType = MarkerType.TEXT
            }
        }
    }
}