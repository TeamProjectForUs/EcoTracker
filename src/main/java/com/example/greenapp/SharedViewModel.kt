package com.example.greenapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.greenapp.Model.Model
import com.example.greenapp.Model.UserModelFirebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import java.lang.Exception

class SharedViewModel : ViewModel() {

    private val _currentUser: MutableLiveData<UserModelFirebase> = MutableLiveData()
    val currentUser get(): LiveData<UserModelFirebase> = _currentUser


    private val _exceptions: MutableLiveData<Exception> = MutableLiveData()
    val exceptions get(): LiveData<Exception> = _exceptions

    private var userListener: ListenerRegistration? = null


    private val authStateListener: FirebaseAuth.AuthStateListener = FirebaseAuth.AuthStateListener {
        if (it.currentUser != null) { // user is logged
            if (userListener != null) {
                userListener?.remove()
                userListener = null
            }

            userListener = Model.instance.getFireBaseModel()
                .addUserListener(
                    onUser = { user ->
                        _currentUser.postValue(user)
                    },
                    onError = { error ->
                        _exceptions.postValue(error)
                    })
        } else { // remove the user listener (logged out)
            if (userListener != null) {
                userListener?.remove()
                userListener = null
            }
        }
    }


    init {
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
    }


    override fun onCleared() {
        super.onCleared()
        userListener?.remove()
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener)
    }

}