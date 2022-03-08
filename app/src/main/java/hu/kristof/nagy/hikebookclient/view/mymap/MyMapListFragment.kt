package hu.kristof.nagy.hikebookclient.view.mymap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.hikebookclient.R
import com.example.hikebookclient.databinding.FragmentMyMapListBinding

class MyMapListFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentMyMapListBinding =DataBindingUtil.inflate(
            inflater, R.layout.fragment_my_map_list, container, false
        )

        binding.myMapRecyclerView.adapter = MyMapListAdapter()

        return binding.root
    }
}