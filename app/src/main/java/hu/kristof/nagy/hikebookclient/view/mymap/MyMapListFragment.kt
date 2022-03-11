package hu.kristof.nagy.hikebookclient.view.mymap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.hikebookclient.R
import com.example.hikebookclient.databinding.FragmentMyMapListBinding

class MyMapListFragment : Fragment() {
    private lateinit var binding: FragmentMyMapListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_my_map_list, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.myMapRecyclerView.adapter = MyMapListAdapter()

        binding.switchToMyMapButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_myMapListFragment_to_myMapFragment
            )
        }
    }
}