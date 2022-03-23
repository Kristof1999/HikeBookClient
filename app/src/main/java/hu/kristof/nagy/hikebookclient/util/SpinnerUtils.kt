package hu.kristof.nagy.hikebookclient.util

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Spinner
import hu.kristof.nagy.hikebookclient.R

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
}