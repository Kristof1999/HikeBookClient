package hu.kristof.nagy.hikebookclient.view.mymap

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.*

/**
 * A Fragment with which the user can choose a date for the group hike.
 */
class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
    private var _dateRes = MutableLiveData<Calendar>()
    val dateRes: LiveData<Calendar>
        get() = _dateRes

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(), this, year, month, day
        ).apply {
            datePicker.minDate = c.timeInMillis
        }
        return datePickerDialog
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
       _dateRes.value = Calendar.getInstance().apply {
           clear()
           set(Calendar.YEAR, year)
           set(Calendar.MONTH, month)
           set(Calendar.DAY_OF_MONTH, day)
       }
    }
}