package com.gamdestroyerr.accelathonapp.views.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.gamdestroyerr.accelathonapp.R
import com.gamdestroyerr.accelathonapp.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var registerBinding: FragmentRegisterBinding
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerBinding = FragmentRegisterBinding.bind(view)
        navController = Navigation.findNavController(view)

        registerBinding.signUpBtn.setOnClickListener {
            navController.navigate(R.id.action_registerFragment_to_mainActivity)
        }
    }
}