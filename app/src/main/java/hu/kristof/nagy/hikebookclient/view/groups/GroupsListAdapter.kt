package hu.kristof.nagy.hikebookclient.view.groups

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hu.kristof.nagy.hikebookclient.databinding.GroupsListItemBinding

class GroupsListAdapter(
    private val isConnectedPage: Boolean,
    private val clickListener: GroupsClickListener
    ): ListAdapter<String, GroupsListAdapter.ViewHolder>(GroupsListDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, isConnectedPage, clickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class ViewHolder(
        private val binding: GroupsListItemBinding,
        private val isConnectedPage: Boolean,
        private val clickListener: GroupsClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        private val button: Button = binding.groupsListItemButton

        fun bind(groupName: String) {
            binding.groupName = groupName
            binding.isConnectedPage = isConnectedPage
            binding.clickListener = clickListener
            if (isConnectedPage) {
                button.text = "Elhagyás"
            } else {
                button.text = "Csatlakozás"
            }
        }

        companion object {
            fun from(
                parent: ViewGroup,
                isConnectedPage: Boolean,
                clickListener: GroupsClickListener
            ): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = GroupsListItemBinding.inflate(
                    layoutInflater, parent, false
                )
                return ViewHolder(binding, isConnectedPage, clickListener)
            }
        }
    }
}

class GroupsListDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}

class GroupsClickListener(
    private val connectListener: (groupName:String, isConnectedPage: Boolean) -> Unit,
    private val detailListener: (groupName: String, isConnectedPage: Boolean) -> Unit
) {
    fun onConnect(groupName: String, isConnectedPage: Boolean) = connectListener(groupName, isConnectedPage)
    fun onDetail(groupName: String, isConnectedPage: Boolean) = detailListener(groupName, isConnectedPage)
}