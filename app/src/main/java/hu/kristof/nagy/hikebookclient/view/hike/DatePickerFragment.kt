// based on:
// https://developer.android.com/guide/topics/ui/controls/pickers#DatePicker

package hu.kristof.nagy.hikebookclient.view.hike

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.*

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
    private var _yearRes = MutableLiveData<Int>()
    val yearRes: LiveData<Int>
        get() = _yearRes

    private var _monthRes = MutableLiveData<Int>()
    val monthRes: LiveData<Int>
        get() = _monthRes

    private var _dayRes = MutableLiveData<Int>()
    val dayRes: LiveData<Int>
        get() = _dayRes

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(requireContext(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        _yearRes.value = year
        _monthRes.value = month
        _dayRes.value = day
    }
}