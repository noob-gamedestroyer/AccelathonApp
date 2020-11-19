package com.gamdestroyerr.accelathonapp.views.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.gamdestroyerr.accelathonapp.R
import com.gamdestroyerr.accelathonapp.databinding.FragmentLoginBinding
import com.gamdestroyerr.accelathonapp.util.EventObserver
import com.gamdestroyerr.accelathonapp.util.snackBar
import com.gamdestroyerr.accelathonapp.util.toast
import com.gamdestroyerr.accelathonapp.viewModels.AuthViewModel
import com.gamdestroyerr.accelathonapp.views.activity.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var loginBinding: FragmentLoginBinding
    private lateinit var navController: NavController
    private lateinit var viewModel: AuthViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginBinding = FragmentLoginBinding.bind(view)
        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        navController = Navigation.findNavController(view)
        subscribeToObserver()
        loginBinding.signInBtn.setOnClickListener {
            viewModel.login(
                    loginBinding.emailTxtInputLayout.editText?.text.toString().trim(),
                    loginBinding.passwordTxtInputLayout.editText?.text.toString().trim(),
            )
        }
        loginBinding.registerBtn.setOnClickListener {
            navController.navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }
    }

    private fun subscribeToObserver() {
        viewModel.loginStatus.observe(viewLifecycleOwner, EventObserver(
                onError = {
                    loginBinding.progressBarLogin.isVisible = false
                    snackBar(it)
                },
                onLoading = { loginBinding.progressBarLogin.isVisible = true }
        ) {
            loginBinding.progressBarLogin.isVisible = false
            toast("Successfully Logged In")
            Intent(requireContext(), MainActivity::class.java).also {
                startActivity(it)
                requireActivity().finish()
            }
        })
    }
}