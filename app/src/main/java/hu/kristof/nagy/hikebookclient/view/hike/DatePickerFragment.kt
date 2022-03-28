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
    private var _dateRes = MutableLiveData<String>()
    val dateRes: LiveData<String>
        get() = _dateRes

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =  DatePickerDialog(requireContext(), this, year, month, day)
        datePickerDialog.datePicker.minDate = c.timeInMillis
        c.add(Calendar.DAY_OF_MONTH, 5)
        datePickerDialog.datePicker.maxDate = c.timeInMillis
        return datePickerDialog
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        if (month < 10) {
            if (day < 10) {
                _dateRes.value = "$year-0${month + 1}-0$day"
            } else {
                _dateRes.value = "$year-0${month + 1}-$day"
            }
        } else {
            if (day < 10) {
                _dateRes.value = "$year-${month + 1}-0$day"
            } else {
                _dateRes.value = "$year-${month + 1}-$day"
            }
        }
    }
}