package hu.kristof.nagy.hikebookclient.model

import hu.kristof.nagy.hikebookclient.model.routes.Route

data class EditedRoute(val newRoute: Route, val oldRoute: Route)