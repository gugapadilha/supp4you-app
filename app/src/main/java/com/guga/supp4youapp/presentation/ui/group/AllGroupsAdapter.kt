package com.guga.supp4youapp.presentation.ui.group

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.guga.supp4youapp.R

class AllGroupsAdapter(private val groupList: List<GroupModel>) : RecyclerView.Adapter<AllGroupsAdapter.GroupViewHolder>() {

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupNameTextView: TextView = itemView.findViewById(R.id.tv_group_name)
        val groupCodeTextView: TextView = itemView.findViewById(R.id.tv_group_code)
        val beginTime: TextView = itemView.findViewById(R.id.tv_begin_time)
        val endTime: TextView = itemView.findViewById(R.id.tv_end_time)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_groups, parent, false)
        return GroupViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = groupList[position]

        holder.groupNameTextView.text = "Group: " + group.groupName
        holder.groupCodeTextView.text = "Code: " +String.format("%04d", group.groupCode)

        // Define um OnClickListener para lidar com a ação de clicar em um grupo
        holder.itemView.setOnClickListener {
        // Implemente a ação que você deseja quando um grupo é clicado
        }
    }

    override fun getItemCount(): Int {
        return groupList.size
    }
}
