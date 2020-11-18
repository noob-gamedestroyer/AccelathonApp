package com.gamdestroyerr.accelathonapp.views.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.gamdestroyerr.accelathonapp.R
import com.gamdestroyerr.accelathonapp.databinding.FragmentLoginBinding

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var loginBinding: FragmentLoginBinding
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginBinding = FragmentLoginBinding.bind(view)
        navController = Navigation.findNavController(view)
        loginBinding.signInBtn.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_mainActivity)
        }
        loginBinding.registerBtn.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }
}