// based on:
// https://developer.android.com/training/basics/network-ops/managing

package hu.kristof.nagy.hikebookclient.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.widget.Toast
import androidx.core.content.ContextCompat
import hu.kristof.nagy.hikebookclient.R

// TODO: avoid spamming the user:
// request a lifecycleOwner too and only show the error message
// once per screen and lifetime

fun handleOffline(context: Context, f: () -> Unit) {
    if (isOnline(context)) {
        f.invoke()
    } else {
        Toast.makeText(
            context,
            context.getString(R.string.offline_once_error_msg),
            Toast.LENGTH_LONG
        ).show()
    }
}

fun handleOfflineLoad(context: Context, f: () -> Unit) {
    if (isOnline(context)) {
        f.invoke()
    } else {
        Toast.makeText(
            context,
            context.getString(R.string.offline_load_error_msg),
            Toast.LENGTH_LONG
        ).show()
    }
}

private fun isOnline(context: Context): Boolean {
    val connMgr = ContextCompat.getSystemService(context, ConnectivityManager::class.java)
    val networkInfo: NetworkInfo? = connMgr?.activeNetworkInfo
    return networkInfo?.isConnected == true
}