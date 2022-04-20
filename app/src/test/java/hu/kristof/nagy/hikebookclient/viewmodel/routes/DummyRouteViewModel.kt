package hu.kristof.nagy.hikebookclient.viewmodel.routes

import hu.kristof.nagy.hikebookclient.model.MyMarker
import hu.kristof.nagy.hikebookclient.viewModel.routes.RouteViewModel
import org.osmdroid.views.overlay.Polyline

class DummyRouteViewModel : RouteViewModel() {
    public override val markers: MutableList<MyMarker> = ArrayList()
    public override val polylines: MutableList<Polyline> = ArrayList()
}