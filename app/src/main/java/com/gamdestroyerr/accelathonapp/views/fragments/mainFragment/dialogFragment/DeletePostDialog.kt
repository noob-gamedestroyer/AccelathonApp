package com.gamdestroyerr.accelathonapp.views.fragments.mainFragment.dialogFragment

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.gamdestroyerr.accelathonapp.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DeletePostDialog : DialogFragment() {

    private var positiveListener: (() -> Unit)? = null

    fun setPositiveListener(listener: () -> Unit) {
        positiveListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Post?")
                .setMessage("  You can't undo this action.")
                .setIcon(R.drawable.ic_round_delete_24)
                .setPositiveButton("Delete") { _, _ ->
                    positiveListener?.let { click ->
                        click()
                    }
                }
                .setNegativeButton("Cancel") { dialogInterface, _ ->
                    dialogInterface.cancel()
                }
                .create()
    }
}