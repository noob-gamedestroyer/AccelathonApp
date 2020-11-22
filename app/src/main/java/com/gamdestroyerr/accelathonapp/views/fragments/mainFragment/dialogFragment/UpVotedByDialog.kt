package com.gamdestroyerr.accelathonapp.views.fragments.mainFragment.dialogFragment

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gamdestroyerr.accelathonapp.views.fragments.adapter.UserAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class UpVotedByDialog(
        private val userAdapter: UserAdapter
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val rvUpVotedBy = RecyclerView(requireContext()).apply {
            adapter = userAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        return MaterialAlertDialogBuilder(requireContext())
                .setTitle("UpVoted By:")
                .setView(rvUpVotedBy)
                .create()
    }
}