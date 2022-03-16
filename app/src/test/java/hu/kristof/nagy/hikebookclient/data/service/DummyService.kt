package hu.kristof.nagy.hikebookclient.data.service

import hu.kristof.nagy.hikebookclient.di.Service
import hu.kristof.nagy.hikebookclient.model.BrowseListItem
import hu.kristof.nagy.hikebookclient.model.Point
import hu.kristof.nagy.hikebookclient.model.Route
import hu.kristof.nagy.hikebookclient.model.UserAuth

class DummyService : Service {
    override suspend fun login(user: UserAuth): Boolean {
        return true
    }

    override suspend fun register(user: UserAuth): Boolean {
        return true
    }

    override suspend fun createRoute(
        userName: String,
        routeName: String,
        points: List<Point>
    ): Boolean {
        return true
    }

    override suspend fun loadRoutesForUser(userName: String): List<Route> {
        return listOf()
    }

    override suspend fun loadPoints(userName: String, routeName: String): List<Point> {
        return listOf()
    }

    override suspend fun listRoutes(): List<BrowseListItem> {
        return listOf()
    }

    override suspend fun deleteRoute(userName: String, routeName: String): Boolean {
        return true
    }

    override suspend fun editRoute(userName: String, oldRouteName: String, route: Route): Boolean {
        return true
    }
}