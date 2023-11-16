package com.guga.supp4youapp.presentation.ui.group

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object GroupManager {
    private val enteredGroups: MutableList<GroupModel> = mutableListOf()
    private const val PREF_ENTERED_GROUPS= "MyPreferences"
    private const val ENTERED_GROUPS_KEY = "MyPreferences"


    fun addGroup(group: GroupModel) {
        // Verifica se o grupo já está na lista
        val isGroupAlreadyAdded = enteredGroups.any { it.groupCode == group.groupCode }

        if (!isGroupAlreadyAdded) {
            enteredGroups.add(group)
        }
    }

    fun getEnteredGroups(): List<GroupModel> {
        return enteredGroups
    }

    fun saveEnteredGroups(contexto: Context) {
        val sharedPreferences: SharedPreferences = contexto.getSharedPreferences(
            "MyPreferences",
            Context.MODE_PRIVATE
        )
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        // Converter a lista de grupos participados para uma string e salvar no SharedPreferences
        val gruposString = Gson().toJson(enteredGroups)
        editor.putString(ENTERED_GROUPS_KEY, gruposString)
        editor.apply()
    }

    fun loadEnteredGroups(context: Context) {
        val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val groupsJson = sharedPreferences.getString(ENTERED_GROUPS_KEY, null)

        if (groupsJson != null) {
            val groupsType = object : TypeToken<List<GroupModel>>() {}.type
            val groups: List<GroupModel> = Gson().fromJson(groupsJson, groupsType)

            enteredGroups.clear()
            enteredGroups.addAll(groups)
        }
    }

}
