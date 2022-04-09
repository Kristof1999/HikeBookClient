package hu.kristof.nagy.hikebookclient.view.groups

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hu.kristof.nagy.hikebookclient.databinding.GroupsDetailListItemBinding

class GroupsDetailListAdapter(
    private val clickListener: GroupsDetailListClickListener
    )
    : ListAdapter<String, GroupsDetailListAdapter.ViewHolder>(GroupsDetailListDiffCallback()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, clickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class ViewHolder(
        private val binding: GroupsDetailListItemBinding,
        private val clickListener: GroupsDetailListClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(routeName: String) {
            binding.routeName = routeName
            binding.clickListener = clickListener
        }

        companion object {
            fun from(
                parent: ViewGroup,
                clickListener: GroupsDetailListClickListener
            ): ViewHolder {
                // TODO: replace boilerplate code with generic lambda across other adapters too
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = GroupsDetailListItemBinding.inflate(
                    layoutInflater, parent, false
                )
                return ViewHolder(binding, clickListener)
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

class GroupsDetailListClickListener(
    private val editListener: (routeName: String) -> Unit,
    private val deleteListener: (routeName: String) -> Unit
) {
    fun onEdit(routeName: String) = editListener(routeName)
    fun onDelete(routeName: String) = deleteListener(routeName)
}