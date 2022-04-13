package hu.kristof.nagy.hikebookclient.view.grouphike

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.kristof.nagy.hikebookclient.databinding.GroupHikeListItemBinding

class GroupHikeListAdapter(
    private val isConnectedPage: Boolean,
    private val clickListener: GroupHikeClickListener
) : RecyclerView.Adapter<GroupHikeListAdapter.ViewHolder>() {
    private val data = listOf("csoport túra 1", "csoport túra 2")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, isConnectedPage, clickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = data.size

    class ViewHolder(
        private val binding: GroupHikeListItemBinding,
        private val isConnectedPage: Boolean,
        private val clickListener: GroupHikeClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(groupHikeName: String) {
            binding.groupHikeName = groupHikeName
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

class GroupHikeClickListener(
    private val generalConnectListener: (groupHikeName: String) -> Unit,
    private val detailNavListener: (groupHikeName: String) -> Unit
) {
    fun onGeneralConnect(groupHikeName: String) = generalConnectListener(groupHikeName)
    fun onDetailNav(groupHikeName: String) = detailNavListener(groupHikeName)
}