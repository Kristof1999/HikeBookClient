package hu.kristof.nagy.hikebookclient.viewmodel.hike

import hu.kristof.nagy.hikebookclient.model.weather.ListResponse
import hu.kristof.nagy.hikebookclient.model.weather.WeatherResponse

class HikePlanDateViewModelTest {

    private val response = initWeatherResponse()

    fun `find first date`() {

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

    fun initWeatherResponse(startDtTxt: String): WeatherResponse {
        val daySize = 3 * 8
        val responseLength = daySize * 5



        return WeatherResponse(
            null, null, null, listOf(

            ), null
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