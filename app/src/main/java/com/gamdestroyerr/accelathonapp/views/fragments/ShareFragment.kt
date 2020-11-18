package com.gamdestroyerr.accelathonapp.views.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.gamdestroyerr.accelathonapp.R
import com.gamdestroyerr.accelathonapp.databinding.FragmentShareBinding
import com.gamdestroyerr.accelathonapp.views.activity.MainActivity

class ShareFragment : Fragment(R.layout.fragment_share) {
    private lateinit var shareBinding: FragmentShareBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as MainActivity
        activity.binding.addFab.hide()
        shareBinding = FragmentShareBinding.bind(view)
    }
}