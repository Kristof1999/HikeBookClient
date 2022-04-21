package hu.kristof.nagy.hikebookclient.view.routes

import android.content.Context
import android.widget.Spinner
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.util.setSpinnerAdapter
import hu.kristof.nagy.hikebookclient.view.mymap.MarkerType
import hu.kristof.nagy.hikebookclient.viewModel.routes.RouteViewModel

fun setMarkerSpinnerAdapter(context: Context, spinner: Spinner) {
    setSpinnerAdapter(context, spinner, R.array.markers)
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
            val dialogFragment = TextDialogFragment.instanceOf(
                R.string.marker_text_dialog_text, R.string.marker_text_dialog_hint
            )
            dialogFragment.text.observe(viewLifecycleOwner) { title ->
                viewModel.markerTitle = title
            }
            dialogFragment.show(parentFragmentManager, "text")
            viewModel.markerType = MarkerType.TEXT
        }
    }
}