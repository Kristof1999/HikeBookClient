package hu.kristof.nagy.hikebookclient.model.routes

/**
 * A Class representing a pair of routes: before and after editing.
 */
open class EditedRoute(val newRoute: Route, val oldRoute: Route)