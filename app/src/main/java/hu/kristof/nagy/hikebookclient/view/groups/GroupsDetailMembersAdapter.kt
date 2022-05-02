package hu.kristof.nagy.hikebookclient.view.groups

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hu.kristof.nagy.hikebookclient.databinding.GroupsDetailMembersListItemBinding

class GroupsDetailMembersAdapter
    : ListAdapter<String, GroupsDetailMembersAdapter.ViewHolder>(GroupsDetailMembersDiffCallback())  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class ViewHolder(
        binding: GroupsDetailMembersListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private val memberNameTv = binding.groupsDetailMemberNameTv

        fun bind(memberName: String) {
            memberNameTv.text = memberName
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = GroupsDetailMembersListItemBinding.inflate(
                    layoutInflater, parent, false
                )
                return ViewHolder(binding)
            }
        }
    }
}

class GroupsDetailMembersDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}