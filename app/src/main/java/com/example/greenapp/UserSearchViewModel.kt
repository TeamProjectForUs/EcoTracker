package com.example.greenapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.greenapp.Model.Model
import com.example.greenapp.Model.UserModelFirebase

class UserSearchViewModel : ViewModel() {

    val userLoadingState = MutableLiveData(Model.LoadingState.LOADED)
    val userResults = MutableLiveData<List<UserModelFirebase>>(listOf())
    val allUsers = MutableLiveData<List<UserModelFirebase>>(listOf())

    init {
        Model.instance.getFireBaseModel().getAllUsers {
            allUsers.postValue(it)
            userLoadingState.postValue(Model.LoadingState.LOADED)
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