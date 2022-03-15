package hu.kristof.nagy.hikebookclient.view.browse

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.hikebookclient.R
import com.example.hikebookclient.databinding.BrowseListItemBinding

class BrowseListAdapter : RecyclerView.Adapter<BrowseListAdapter.ViewHolder>() {

    private val userData = listOf("user1", "user2")
    private val routeData = listOf("route1", "route2")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userName = userData[position]
        val routeName = routeData[position]
        holder.bind(userName, routeName)
    }

    override fun getItemCount(): Int = userData.size

    class ViewHolder(binding: BrowseListItemBinding)
        : RecyclerView.ViewHolder(binding.root) {
        private val userNameTv: TextView = binding.browseListItemUserName
        private val routeNameTv: TextView = binding.browseListItemRouteName

        init {
            itemView.setOnClickListener {
                val userName = userNameTv.text.toString()
                val routeName = routeNameTv.text.toString()
                val action = BrowseListFragmentDirections
                    .actionBrowseListFragmentToBrowseDetailFragment(userName, routeName)
                it.findNavController().navigate(action)
            }
        }

        fun bind(userName: String, routeName: String) {
            userNameTv.text = userName
            routeNameTv.text = routeName
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DataBindingUtil.inflate<BrowseListItemBinding>(
                    layoutInflater, R.layout.browse_list_item, parent, false
                )
                return ViewHolder(binding)
            }
        }
    }
}