package hu.kristof.nagy.hikebookclient.view.groups

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hu.kristof.nagy.hikebookclient.databinding.FragmentGroupsListItemBinding

class GroupsListAdapter(private val isConnectedPage: Boolean)
    : RecyclerView.Adapter<GroupsListAdapter.ViewHolder>() {
    private val data = listOf("csoport 1", "csoport 2")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, isConnectedPage)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = data.size

    class ViewHolder(
        binding: FragmentGroupsListItemBinding,
        private val isConnectedPage: Boolean
    ) : RecyclerView.ViewHolder(binding.root) {
        val name: TextView = binding.groupsListItemGroupNameButton
        val button: Button = binding.groupsListItemButton

        fun bind(groupName: String) {
            name.text = groupName
            if (isConnectedPage) {
                button.text = "Elhagyás"
            } else {
                button.text = "Csatlakozás"
            }
        }

        companion object {
            fun from(parent: ViewGroup, isConnectedPage: Boolean): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FragmentGroupsListItemBinding.inflate(
                    layoutInflater, parent, false
                )
                return ViewHolder(binding, isConnectedPage)
            }
        }
    }

}