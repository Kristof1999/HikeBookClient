package hu.kristof.nagy.hikebookclient.view.grouphike

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hu.kristof.nagy.hikebookclient.databinding.GroupHikeDetailParticipantsListItemBinding

class GroupHikeDetailParticipantsListAdapter
    : ListAdapter<String, GroupHikeDetailParticipantsListAdapter.ViewHolder>(GroupHikeParticipantDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class ViewHolder(
        private val binding: GroupHikeDetailParticipantsListItemBinding
        ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(participantName: String) {
            binding.groupsHikeDetailMembersListItemMemberNameTv.text = participantName
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = GroupHikeDetailParticipantsListItemBinding.inflate(
                    layoutInflater, parent, false
                )
                return ViewHolder(binding)
            }
        }
    }
}

class GroupHikeParticipantDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}