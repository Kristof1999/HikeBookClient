package hu.kristof.nagy.hikebookclient.view.groups

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.databinding.GroupCreateDialogBinding

class GroupCreateDialogFragment : DialogFragment() {
    private var _name = MutableLiveData<String>()
    val name: LiveData<String>
        get() = _name

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val binding = DataBindingUtil.inflate<GroupCreateDialogBinding>(
                inflater, R.layout.group_create_dialog, null, false
            )
            builder.setView(binding.root)
                .setPositiveButton("OK") { _, _ ->
                    _name.value = binding.groupCreateDialogEditText.text.toString()
                }
                .setNegativeButton("Mégse") { _, _ ->
                    dialog?.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}