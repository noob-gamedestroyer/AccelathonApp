package com.gamdestroyerr.accelathonapp.viewmodels

import android.content.Context
import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamdestroyerr.accelathonapp.R
import com.gamdestroyerr.accelathonapp.repositories.MainRepository
import com.gamdestroyerr.accelathonapp.util.Event
import com.gamdestroyerr.accelathonapp.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShareToNgoViewModel @ViewModelInject constructor(
    private val repository: MainRepository,
    private val applicationContext: Context,
    private val dispatchers: CoroutineDispatcher = Dispatchers.Main,
) : ViewModel() {
    private val _createNgoPostStatus = MutableLiveData<Event<Resource<Any>>>()
    val createNgoPostStatus: LiveData<Event<Resource<Any>>> = _createNgoPostStatus

    private val _curImageUri = MutableLiveData<Uri>()
    val curImageUri: LiveData<Uri> = _curImageUri

    fun setCurImageUri(uri: Uri) {
        _curImageUri.postValue(uri)
    }

    fun createNgoPost(imageUri: Uri, text: String, ngo: String) {
        if (text.isEmpty() || ngo.isEmpty()) {
            val error = applicationContext.getString(R.string.error_input_empty)
            _createNgoPostStatus.postValue(Event(Resource.Error(error)))
        } else {
            _createNgoPostStatus.postValue(Event(Resource.Loading()))
            viewModelScope.launch(dispatchers) {
                val result = repository.createNgoPost(imageUri, text, ngo)
                _createNgoPostStatus.postValue(Event(result))
            }
        }
    }

}