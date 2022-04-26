// based on:
// https://developer.android.com/guide/topics/ui/dialogs

package hu.kristof.nagy.hikebookclient.view

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import hu.kristof.nagy.hikebookclient.databinding.TextDialogBinding

/**
 * A Fragment to set the text of something.
 */
class TextDialogFragment : DialogFragment() {
    private var _text = MutableLiveData<String>()
    val text: LiveData<String>
        get() = _text

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val binding = TextDialogBinding.inflate(inflater, null, false)

            adaptView(binding)

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

    private fun adaptView(binding: TextDialogBinding) {
        binding.apply {
            val textViewTextId = arguments?.getInt(TEXT_VIEW_BUNDLE_KEY)!!
            val editTextHintId = arguments?.getInt(EDIT_TEXT_BUNDLE_KEY)!!
            textDialogTv.text = requireContext().getText(textViewTextId)
            textDialogEditText.setHint(editTextHintId)
        }
    }

    companion object {
        private const val TEXT_VIEW_BUNDLE_KEY = "textView"
        private const val EDIT_TEXT_BUNDLE_KEY = "editText"

        fun instanceOf(textViewTextId: Int, editTextHintId: Int): TextDialogFragment {
            return TextDialogFragment().apply {
                val bundle = bundleOf(
                    TEXT_VIEW_BUNDLE_KEY to textViewTextId,
                    EDIT_TEXT_BUNDLE_KEY to editTextHintId
                )
                arguments = bundle
            }
        }
    }
}