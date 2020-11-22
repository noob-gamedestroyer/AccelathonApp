package com.gamdestroyerr.accelathonapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamdestroyerr.accelathonapp.model.Post
import com.gamdestroyerr.accelathonapp.model.User
import com.gamdestroyerr.accelathonapp.repositories.MainRepository
import com.gamdestroyerr.accelathonapp.util.Event
import com.gamdestroyerr.accelathonapp.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class BasePostViewModel(
        private val repository: MainRepository,
        private val dispatcher: CoroutineDispatcher = Dispatchers.Main,
) : ViewModel() {

    private val _upVotePostStatus = MutableLiveData<Event<Resource<Boolean>>>()
    val upVotePostStatus: LiveData<Event<Resource<Boolean>>> = _upVotePostStatus

    private val _deletePostStatus = MutableLiveData<Event<Resource<Post>>>()
    val deletePostStatus: LiveData<Event<Resource<Post>>> = _deletePostStatus

    private val _upVotedByUserStatus = MutableLiveData<Event<Resource<List<User>>>>()
    val upVotedByUserStatus: LiveData<Event<Resource<List<User>>>> = _upVotedByUserStatus

    fun getUsers(uids: List<String>) {
        if (uids.isEmpty()) return
        _upVotedByUserStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.getUsers(uids)
            _upVotedByUserStatus.postValue(Event(result))
        }
    }

    fun toggleUpVoteForPost(post: Post) {
        _upVotePostStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.toggleUpVoteForPost(post)
            _upVotePostStatus.postValue(Event(result))
        }
    }

    fun deletePost(post: Post) {
        _deletePostStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.deletePost(post)
            _deletePostStatus.postValue(Event(result))
        }
    }

    abstract val posts: LiveData<Event<Resource<List<Post>>>>

    abstract fun getPosts(uid: String = "")
}