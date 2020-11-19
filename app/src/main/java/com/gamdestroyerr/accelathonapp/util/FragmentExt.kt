package com.gamdestroyerr.accelathonapp.util

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun Fragment.snackBar(text: String) {
    Snackbar.make(
            requireView(),
            text,
            Snackbar.LENGTH_LONG
    ).show()
}

fun Fragment.toast(message: String) {
    Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_LONG
    ).show()
}