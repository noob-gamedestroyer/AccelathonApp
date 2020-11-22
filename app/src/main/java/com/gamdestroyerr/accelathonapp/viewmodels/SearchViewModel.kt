package com.gamdestroyerr.accelathonapp.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamdestroyerr.accelathonapp.model.User
import com.gamdestroyerr.accelathonapp.repositories.MainRepository
import com.gamdestroyerr.accelathonapp.util.Event
import com.gamdestroyerr.accelathonapp.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel @ViewModelInject constructor(
        private val repository: MainRepository,
        private val dispatchers: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private val _searchResults = MutableLiveData<Event<Resource<List<User>>>>()
    val searchResults: LiveData<Event<Resource<List<User>>>> = _searchResults

    fun searchUser(query: String) {
        if (query.isEmpty()) return

        _searchResults.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatchers) {
            val result = repository.searchUser(query)
            _searchResults.postValue(Event(result))
        }
    }
}