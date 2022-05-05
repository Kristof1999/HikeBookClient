// based on:
// https://developer.android.com/training/basics/network-ops/managing

package hu.kristof.nagy.hikebookclient.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.model.ResponseResult

/**
 * If the device is online, it executes the given lambda,
 * otherwise it shows an error message.
 */
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

/**
 * If the device is online, it executes the given lambda,
 * otherwise it sets an error for the provided MutableLiveData object.
 */
suspend fun <T> handleOffline(
    data: MutableLiveData<ResponseResult<T>>,
    context: Context,
    f: suspend () -> Unit
) {
    if (isOnline(context)) {
        f.invoke()
    } else {
        data.value = ResponseResult.Error(context.getString(R.string.offline_once_error_msg))
    }
}

/**
 * If the device is online, it executes the given lambda,
 * otherwise it shows an error message.
 */
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