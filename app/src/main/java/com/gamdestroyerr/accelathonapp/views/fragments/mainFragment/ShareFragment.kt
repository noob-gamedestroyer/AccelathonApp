package com.gamdestroyerr.accelathonapp.views.fragments.mainFragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.gamdestroyerr.accelathonapp.R
import com.gamdestroyerr.accelathonapp.databinding.FragmentShareBinding
import com.gamdestroyerr.accelathonapp.util.snackBar
import com.gamdestroyerr.accelathonapp.views.activity.MainActivity

class ShareFragment : Fragment(R.layout.fragment_share) {
    private lateinit var shareBinding: FragmentShareBinding
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as MainActivity
        activity.binding.addFab.isEnabled = false
        shareBinding = FragmentShareBinding.bind(view)
        navController = Navigation.findNavController(view)
        shareBinding.shareWithNgoContainer.setOnClickListener {
            navController.navigate(R.id.action_shareFragment_to_shareToNgoFragment)
        }
        shareBinding.shareWithResidentsContainer.setOnClickListener {
            snackBar(getString(R.string.future_impl))
        }
    }
}