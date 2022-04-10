package hu.kristof.nagy.hikebookclient.util

import android.content.Context
import android.widget.Toast
import hu.kristof.nagy.hikebookclient.R

fun throwGenericErrorOr(context: Context?, res:Boolean, msg: String) {
    if (res) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(
            context, context?.getText(R.string.generic_error_msg), Toast.LENGTH_LONG
        ).show()
    }
}

fun throwGenericErrorOr(context: Context?, res:Boolean, f: () -> Unit) {
    if (res) {
        f.invoke()
    } else {
        Toast.makeText(
            context, context?.getText(R.string.generic_error_msg), Toast.LENGTH_LONG
        ).show()
    }
}