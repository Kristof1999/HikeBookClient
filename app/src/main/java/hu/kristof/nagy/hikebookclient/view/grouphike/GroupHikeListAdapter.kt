package hu.kristof.nagy.hikebookclient.view.grouphike

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hu.kristof.nagy.hikebookclient.databinding.GroupHikeListItemBinding
import hu.kristof.nagy.hikebookclient.model.DateTime
import hu.kristof.nagy.hikebookclient.model.GroupHikeListHelper

class GroupHikeListAdapter(
    private val isConnectedPage: Boolean,
    private val clickListener: GroupHikeClickListener
) : ListAdapter<GroupHikeListHelper, GroupHikeListAdapter.ViewHolder>(GroupHikeDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, isConnectedPage, clickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class ViewHolder(
        private val binding: GroupHikeListItemBinding,
        private val isConnectedPage: Boolean,
        private val clickListener: GroupHikeClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(helper: GroupHikeListHelper) {
            binding.groupHikeName = helper.groupHikeName
            binding.dateTimeStr = helper.dateTime.toString()
            binding.dateTimeObj = helper.dateTime
            binding.clickListener = clickListener
            binding.groupHikeListItemGeneralConnectButton.apply {
                if (isConnectedPage) {
                    text = "Elhagyás"
                } else {
                    text = "Csatlakozás"
                }
            }
        }

        companion object {
            fun from(
                parent: ViewGroup,
                isConnectedPage: Boolean,
                clickListener: GroupHikeClickListener
            ): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = GroupHikeListItemBinding.inflate(
                    layoutInflater, parent, false
                )
                return ViewHolder(binding, isConnectedPage, clickListener)
            }
        }
    }
}

class GroupHikeDiffCallback : DiffUtil.ItemCallback<GroupHikeListHelper>() {
    override fun areItemsTheSame(oldItem: GroupHikeListHelper, newItem: GroupHikeListHelper): Boolean {
        return oldItem.groupHikeName == newItem.groupHikeName
    }

    override fun areContentsTheSame(oldItem: GroupHikeListHelper, newItem: GroupHikeListHelper): Boolean {
        return oldItem == newItem
    }
}

class GroupHikeClickListener(
    private val generalConnectListener: (groupHikeName: String, dateTime: DateTime) -> Unit,
    private val detailNavListener: (groupHikeName: String, dateTime: DateTime) -> Unit
) {
    fun onGeneralConnect(groupHikeName: String, dateTime: DateTime) = generalConnectListener(groupHikeName, dateTime)
    fun onDetailNav(groupHikeName: String, dateTime: DateTime) = detailNavListener(groupHikeName, dateTime)
}