package hu.kristof.nagy.hikebookclient.viewModel.hike

import hu.kristof.nagy.hikebookclient.model.weather.WeatherResponse

/**
 * Finds the index in the given WeatherResponse for the given hour and date.
 */
object DateHourIdxFinder {
    fun findHourIdx(hour: Int, response: WeatherResponse, dayStartIdx: Int): Int {
        for (i in 0..7) {
            val hourResponse = response?.list?.get(dayStartIdx + i)?.dtTxt?.substring(11, 13)!!.toInt()
            val nextHourResponse = response?.list?.get(dayStartIdx + i + 1)?.dtTxt?.substring(11, 13)!!.toInt()
            if (hour in hourResponse..nextHourResponse) {
                // which hourResponse is hour closer to?
                if (hour - hourResponse < nextHourResponse - hour)
                    return dayStartIdx + i
                else
                    return dayStartIdx + i + 1
            }
        }
        throw IndexOutOfBoundsException("$hour cannot be found in the weather response: $response")
    }

    fun findDateIdx(date: String, response: WeatherResponse): Int {
        for (i in response?.list?.indices!!) {
            if (response?.list?.get(i)?.dtTxt?.substring(0, 10) == date)
                return i
        }
        throw IndexOutOfBoundsException("$date cannot be found in the weather response: $response")
    }
}