package com.gamdestroyerr.accelathonapp.viewmodels

import android.content.Context
import android.util.Patterns
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamdestroyerr.accelathonapp.R
import com.gamdestroyerr.accelathonapp.repositories.AuthRepository
import com.gamdestroyerr.accelathonapp.util.Event
import com.gamdestroyerr.accelathonapp.util.Resource
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel @ViewModelInject constructor(
        private val repository: AuthRepository,
        private val applicationContext: Context,
        private val dispatchers: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private val _registerStatus = MutableLiveData<Event<Resource<AuthResult>>>()
    val registerStatus: LiveData<Event<Resource<AuthResult>>> = _registerStatus

    private val _loginStatus = MutableLiveData<Event<Resource<AuthResult>>>()
    val loginStatus: LiveData<Event<Resource<AuthResult>>> = _loginStatus

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            val error = applicationContext.getString(R.string.error_input_empty)
            _loginStatus.postValue(Event(Resource.Error(error)))
        } else {
            _loginStatus.postValue(Event(Resource.Loading()))
            viewModelScope.launch(dispatchers) {
                val result = repository.login(email, password)
                _loginStatus.postValue(Event(result))
            }
        }
    }

    fun register(
        email: String,
        username: String,
        password: String,
        phoneNumber: String,
        apartment: String,
        wing: String,
        flat: String,
    ) {
        val error = if (
            email.isEmpty() ||
            username.isEmpty() ||
            password.isEmpty() ||
            phoneNumber.isEmpty() ||
            apartment.isEmpty() ||
            wing.isEmpty() ||
            flat.isEmpty()
        ) {
            applicationContext.getString(R.string.error_input_empty)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            applicationContext.getString(R.string.error_not_a_valid_email)
        } else null

        error?.let {
            _registerStatus.postValue(Event(Resource.Error(it)))
            return
        }
        _registerStatus.postValue(Event(Resource.Loading()))

        viewModelScope.launch(dispatchers) {
            val result = repository.register(
                email,
                username,
                password,
                phoneNumber,
                apartment,
                wing,
                flat,
            )
            _registerStatus.postValue(Event(result))
        }
    }

}