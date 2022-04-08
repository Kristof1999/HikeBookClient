package hu.kristof.nagy.hikebookclient.util

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import hu.kristof.nagy.hikebookclient.BuildConfig
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView

open class MapFragment : Fragment() {
    protected lateinit var map: MapView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}