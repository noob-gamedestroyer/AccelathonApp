package com.gamdestroyerr.accelathonapp.views.fragments.authFragment

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.gamdestroyerr.accelathonapp.R
import com.gamdestroyerr.accelathonapp.databinding.FragmentRegisterBinding
import com.gamdestroyerr.accelathonapp.util.EventObserver
import com.gamdestroyerr.accelathonapp.util.snackBar
import com.gamdestroyerr.accelathonapp.viewModels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var registerBinding: FragmentRegisterBinding
    private lateinit var navController: NavController
    private lateinit var viewModel: AuthViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerBinding = FragmentRegisterBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)
        navController = Navigation.findNavController(view)
        subscribeToObservers()


        registerBinding.signUpBtn.setOnClickListener {
            viewModel.register(
                registerBinding.emailTxtRegister.editText?.text.toString().trim(),
                registerBinding.nameTxt.editText?.text.toString(),
                registerBinding.setPasswordTxtInput.editText?.text.toString().trim(),
                registerBinding.phoneTxtInputLayout.editText?.text.toString().trim(),
                registerBinding.apartmentTxtInputLayout.editText?.text.toString(),
                registerBinding.wingTxtInputLayout.editText?.text.toString(),
                registerBinding.flatTxtInputLayout.editText?.text.toString(),
            )
        }
    }

    private fun subscribeToObservers() {
        viewModel.registerStatus.observe(viewLifecycleOwner, EventObserver(
                onError = {
                    registerBinding.progressBarRegister.isVisible = false
                    snackBar(it)
                },
                onLoading = {
                    registerBinding.progressBarRegister.isVisible = true
                },
        ) {
            registerBinding.progressBarRegister.isVisible = false
            snackBar(getString(R.string.success_register))
            registerBinding.apply {
                emailTxtRegister.editText?.text?.clear()
                nameTxt.editText?.text?.clear()
                setPasswordTxtInput.editText?.text?.clear()
            }
            navController.popBackStack()
        })
    }
}