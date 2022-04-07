// based on:
// https://developer.android.com/guide/topics/ui/dialogs

package hu.kristof.nagy.hikebookclient.view.routes

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.databinding.TextDialogBinding

class TextDialogFragment : DialogFragment() {
    private var _text = MutableLiveData<String>()
    val text: LiveData<String>
        get() = _text

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val binding = DataBindingUtil.inflate<hu.kristof.nagy.hikebookclient.databinding.TextDialogBinding>(
                inflater, R.layout.text_dialog, null, false
            )
            builder.setView(binding.root)
                .setPositiveButton("OK") { _, _ ->
                    _text.value = binding.textDialogEditText.text.toString()
                }
                .setNegativeButton("Mégse") { _, _ ->
                    dialog?.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}