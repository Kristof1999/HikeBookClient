package hu.kristof.nagy.hikebookclient.viewmodel.hike

import hu.kristof.nagy.hikebookclient.model.weather.ListResponse
import hu.kristof.nagy.hikebookclient.model.weather.WeatherResponse
import hu.kristof.nagy.hikebookclient.viewModel.hike.DateHourIdxFinder
import junit.framework.Assert.assertEquals
import org.junit.Test
import java.util.*

class DateHourIdxFinderTest {

    private val response = initWeatherResponse()

    @Test
    fun `find first date`() {
        val date = "2022-04-20"

        val idx = DateHourIdxFinder.findDateIdx(date, response)

        assertEquals(0, idx)
    }

    fun `find last date`() {

    }

    fun `find inner date`() {

    }

    fun `find first hour`() {

    }

    fun `find last hour`() {

    }

    fun `find inner hour`() {

    }

    fun `illegal date`() {
        // before today
        // after 5 days -> erre odafigyelni view-ban is!

    }

    fun `illegal hour`() {

    }

    fun initWeatherResponse(): WeatherResponse {
        val startDtTxt = "2022-04-20 12:00:00"
        val hourInterval = 3
        val daySize = 8
        val responseLength = daySize * 5

        val c = Calendar.getInstance().apply {
            clear()
            set(Calendar.YEAR, startDtTxt.substring(0, 4).toInt())
            set(Calendar.MONTH, startDtTxt.substring(5, 5 + 2).toInt())
            set(Calendar.DAY_OF_MONTH, startDtTxt.substring(8, 8 + 2).toInt())
            set(Calendar.HOUR_OF_DAY, startDtTxt.substring(11, 11 + 2).toInt())
        }
        val dateTimeList = ArrayList<Calendar>()
        for (i in IntRange(0, responseLength)) {
            dateTimeList.add(c)
            c.roll(Calendar.HOUR_OF_DAY, hourInterval)
        }

        val dtTxtList = dateTimeList.map { dateTime ->
            val year = dateTime.get(Calendar.YEAR).toString()
            val month = dateTime.get(Calendar.MONTH).let { month ->
                if (month < 10)
                    "0$month"
                else
                    "$month"
            }
            val dayOfMonth = dateTime.get(Calendar.DAY_OF_MONTH).let { dayOfMonth ->
                if (dayOfMonth < 10)
                    "0$dayOfMonth"
                else
                    "$dayOfMonth"
            }
            val hourOfDay = dateTime.get(Calendar.HOUR_OF_DAY).let { hourOfDay ->
                if (hourOfDay < 10)
                    "0$hourOfDay"
                else
                    "$hourOfDay"
            }
            "$year-$month-$dayOfMonth $hourOfDay:00:00"
        }
        return WeatherResponse(
            null, null, null, dtTxtList.map { initListResponse(it) }, null
        )
    }

    fun initListResponse(dtTxt: String): ListResponse {
        return ListResponse(
            null, null, null, null, null,
            null, null, null,null,null,
            dtTxt
        )
    }
}