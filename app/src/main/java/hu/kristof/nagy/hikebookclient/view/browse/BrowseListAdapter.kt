package hu.kristof.nagy.hikebookclient.view.browse

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.databinding.BrowseListItemBinding
import hu.kristof.nagy.hikebookclient.model.BrowseListItem

class BrowseListAdapter(private val clickListener: BrowseClickListener)
    : ListAdapter<BrowseListItem, BrowseListAdapter.ViewHolder>(BrowseListDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        val userName = data.userName
        val routeName = data.routeName
        holder.bind(userName, routeName, clickListener)
    }

    class ViewHolder(private val binding: BrowseListItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(userName: String, routeName: String, clickListener: BrowseClickListener) {
            binding.userName = userName
            binding.routeName = routeName
            binding.clickListener = clickListener
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

class BrowseClickListener(
    private val clickListener: (userName: String, routeName: String) -> Unit
) {
   fun onClick(userName: String, routeName: String) = clickListener(userName, routeName)
}