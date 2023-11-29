package com.guga.supp4youapp.presentation.ui.group

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.guga.supp4youapp.databinding.ActivityAllGroupsBinding

class AllGroupsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllGroupsBinding
    private lateinit var allGroupsAdapter: AllGroupsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllGroupsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val enteredGroups = GroupManager.getEnteredGroups()
        allGroupsAdapter = AllGroupsAdapter(enteredGroups)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = allGroupsAdapter


        binding.back.setOnClickListener {
            onBackPressed()
        }

        binding.backIcon.setOnClickListener {
            onBackPressed()
        }

        binding.helpButton.setOnClickListener{
            showHelpDialog()
        }
    }
    private fun showHelpDialog() {
        val dialogMessage = "Here you can see all the groups that you already join and they respective details!"

        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Tips for you!")
            .setMessage(dialogMessage)
            .setPositiveButton("Continue") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }
}