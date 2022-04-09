package hu.kristof.nagy.hikebookclient.view.groups

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hu.kristof.nagy.hikebookclient.databinding.GroupsDetailListItemBinding

class GroupsDetailListAdapter
    : ListAdapter<String, GroupsDetailListAdapter.ViewHolder>(GroupsDetailListDiffCallback()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class ViewHolder(
        binding: GroupsDetailListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private val routeNameTv = binding.groupsDetailListItemRouteNameTv

        fun bind(routeName: String) {
            routeNameTv.text = routeName
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                // TODO: replace boilerplate code with generic lambda across other adapters too
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = GroupsDetailListItemBinding.inflate(
                    layoutInflater, parent, false
                )
                return ViewHolder(binding)
            }
        }
    }
}

class GroupsDetailListDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}