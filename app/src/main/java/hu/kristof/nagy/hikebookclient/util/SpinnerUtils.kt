package hu.kristof.nagy.hikebookclient.util

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Spinner

/**
 * Sets the given spinner's adapter to the given array.
 * The item's and the adapter's layouts are simple builtins.
 */
fun setSpinnerAdapter(context: Context, spinner: Spinner, arrayId: Int) {
    ArrayAdapter.createFromResource(
        context,
        arrayId,
        android.R.layout.simple_spinner_item
    ).also { adapter ->
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }
}