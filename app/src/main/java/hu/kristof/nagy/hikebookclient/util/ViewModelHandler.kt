package hu.kristof.nagy.hikebookclient.util

import android.content.Context
import android.widget.Toast
import hu.kristof.nagy.hikebookclient.R

/**
 * Shows generic error message if the provided result if false,
 * otherwise it shows the provided message.
 * @param res the boolean on which to decide to show the generic error message or not
 * @param msg the message to show if the result is true
 */
fun showGenericErrorOr(context: Context?, res: Boolean, msg: String) {
    if (res) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(
            context, context?.getText(R.string.generic_error_msg), Toast.LENGTH_LONG
        ).show()
    }
}

/**
 * Shows generic error message if the provided result if false,
 * otherwise it executes the given lambda.
 * @param res the boolean on which to decide to show the generic error message or not
 * @param f the lambda to execute if the result is true
 */
fun showGenericErrorOr(context: Context?, res: Boolean, f: () -> Unit) {
    if (res) {
        f.invoke()
    } else {
        Toast.makeText(
            context, context?.getText(R.string.generic_error_msg), Toast.LENGTH_LONG
        ).show()
    }
}