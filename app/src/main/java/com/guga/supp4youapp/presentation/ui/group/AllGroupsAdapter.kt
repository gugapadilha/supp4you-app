package com.guga.supp4youapp.presentation.ui.group

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.guga.supp4youapp.R
import java.text.SimpleDateFormat
import java.util.*

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
        if (!group.beginTime.isNullOrEmpty()) {
            try {
                val beginTimeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val beginTimeDate = beginTimeFormat.parse(group.beginTime)
                holder.beginTime.text = beginTimeFormat.format(beginTimeDate)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            holder.beginTime.text = "00:00"
        }

        if (!group.endTime.isNullOrEmpty()) {
            try {
                val endTimeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val endTimeDate = endTimeFormat.parse(group.endTime)
                holder.endTime.text = endTimeFormat.format(endTimeDate)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            holder.endTime.text = "00:00"
        }

        holder.itemView.setOnClickListener {
        }
    }

    override fun getItemCount(): Int {
        return groupList.size
    }
}
