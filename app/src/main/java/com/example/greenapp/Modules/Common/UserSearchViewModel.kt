package com.example.greenapp.modules.Common

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.greenapp.models.Model
import com.example.greenapp.models.OtherUser

class UserSearchViewModel : ViewModel() {

    val userLoadingState = MutableLiveData(Model.LoadingState.LOADED)
    val userResults = MutableLiveData<List<OtherUser>>(listOf())
    val allUsers = MutableLiveData<List<OtherUser>>()

    init {
        Model.instance.userRepository.getAllUsers(viewModelScope) {
            allUsers.postValue(it)
        }
    }

    fun searchUsers(query: String) {
        if (query.isEmpty()) {
            userResults.postValue(listOf())
            return
        }
        userResults.postValue(allUsers.value?.filter { user ->
            user.name.startsWith(query)
                    || user.email.startsWith(query)
        })
    }
}