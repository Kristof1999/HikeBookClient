package hu.kristof.nagy.hikebookclient.view.browse

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hikebookclient.R
import com.example.hikebookclient.databinding.BrowseListItemBinding
import hu.kristof.nagy.hikebookclient.model.BrowseListItem

class BrowseListAdapter : ListAdapter<BrowseListItem, BrowseListAdapter.ViewHolder>(BrowseListDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        val userName = data.userName
        val routeName = data.routeName
        holder.bind(userName, routeName)
    }

    class ViewHolder(binding: BrowseListItemBinding)
        : RecyclerView.ViewHolder(binding.root) {
        private val userNameTv: TextView = binding.browseListItemUserName
        private val routeNameTv: TextView = binding.browseListItemRouteName

        init {
            // TODO: refactor: move listener logic to fragment
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

class BrowseListDiffCallback : DiffUtil.ItemCallback<BrowseListItem>() {
    override fun areItemsTheSame(oldItem: BrowseListItem, newItem: BrowseListItem): Boolean {
        return oldItem.userName == newItem.userName
                && oldItem.routeName == newItem.routeName
    }

    override fun areContentsTheSame(oldItem: BrowseListItem, newItem: BrowseListItem): Boolean {
        return oldItem == newItem
    }

}