package hu.kristof.nagy.hikebookclient.viewmodel.hike

import hu.kristof.nagy.hikebookclient.model.weather.ListResponse
import hu.kristof.nagy.hikebookclient.model.weather.WeatherResponse
import hu.kristof.nagy.hikebookclient.viewModel.hike.DateHourIdxFinder
import junit.framework.Assert.assertEquals
import org.junit.Assert.assertThrows
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

    @Test
    fun `find last date`() {
        val date = "2022-04-25"

        val idx = DateHourIdxFinder.findDateIdx(date, response)

        assertEquals(8 * 4 + 4, idx)
    }

    @Test
    fun `find inner date`() {
        val date = "2022-04-22"

        val idx = DateHourIdxFinder.findDateIdx(date, response)

        assertEquals(8 + 4, idx)
    }

    @Test
    fun `find first hour on first day`() {
        val date = "2022-04-20"
        val hour = 12

        val dateIdx = DateHourIdxFinder.findDateIdx(date, response)
        val idx = DateHourIdxFinder.findHourIdx(hour, response, dateIdx)

        assertEquals(0, idx)
    }

    @Test
    fun `find last hour on first day`() {
        val date = "2022-04-20"
        val hour = 21

        val dateIdx = DateHourIdxFinder.findDateIdx(date, response)
        val idx = DateHourIdxFinder.findHourIdx(hour, response, dateIdx)

        assertEquals(3, idx)
    }

    @Test
    fun `find inner hour on first day`() {
        val date = "2022-04-20"
        val hour1 = 13
        val hour2 = 14
        val hour3 = 15

        val dateIdx = DateHourIdxFinder.findDateIdx(date, response)
        val idx1 = DateHourIdxFinder.findHourIdx(hour1, response, dateIdx)
        val idx2 = DateHourIdxFinder.findHourIdx(hour2, response, dateIdx)
        val idx3 = DateHourIdxFinder.findHourIdx(hour3, response, dateIdx)

        assertEquals(0, idx1)
        assertEquals(1, idx2)
        assertEquals(1, idx3)
    }

    @Test
    fun `illegal date`() {
        val dates = listOf(
            "2022-04-19", "2022-03-20", "2000-01-01",
            "hello world", "", "2022-04-26"
        )

        for (date in dates) {
            assertThrows(IndexOutOfBoundsException::class.java) {
                DateHourIdxFinder.findDateIdx(date, response)
            }
        }
    }

    @Test
    fun `illegal hour`() {
        val hour = -1
        val hour2 = 25
        val date = "2022-04-20"
        val dateIdx = DateHourIdxFinder.findDateIdx(date, response)

        assertThrows(IndexOutOfBoundsException::class.java) {
            DateHourIdxFinder.findHourIdx(hour, response, dateIdx)
        }
        assertThrows(IndexOutOfBoundsException::class.java) {
            DateHourIdxFinder.findHourIdx(hour2, response, dateIdx)
        }
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
        dateTimeList.add(c)
        for (i in IntRange(0, responseLength)) {
            val newCalendar = Calendar.getInstance().apply {
                clear()
                dateTimeList.last().let { prevCalendar ->
                    set(Calendar.YEAR, prevCalendar.get(Calendar.YEAR))
                    set(Calendar.MONTH, prevCalendar.get(Calendar.MONTH))
                    set(Calendar.DAY_OF_MONTH, prevCalendar.get(Calendar.DAY_OF_MONTH))
                    set(Calendar.HOUR_OF_DAY, prevCalendar.get(Calendar.HOUR_OF_DAY))
                }
                add(Calendar.HOUR_OF_DAY, hourInterval)
            }
            dateTimeList.add(newCalendar)
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