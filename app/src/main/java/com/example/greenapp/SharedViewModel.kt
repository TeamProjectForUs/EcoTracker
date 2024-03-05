package com.example.greenapp

import android.view.Display.Mode
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.greenapp.Model.Goal
import com.example.greenapp.Model.Model
import com.example.greenapp.Model.Post
import com.example.greenapp.Model.Tip
import com.example.greenapp.Model.UserModelFirebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import java.lang.Exception


// Handles the user + posts
// Common to a lot of screens
class SharedViewModel : ViewModel() {

    val currentUser: MutableLiveData<UserModelFirebase> = MutableLiveData()
    val exceptions: MutableLiveData<Exception> = MutableLiveData()

    private var userListener: ListenerRegistration? = null
    private val _userLoadingState: MutableLiveData<Model.LoadingState> =
        MutableLiveData(Model.LoadingState.LOADED)

    val userLoadingState: LiveData<Model.LoadingState>
        get() =
            _userLoadingState


    val postsListLoadingState: MutableLiveData<Model.LoadingState> =
        MutableLiveData(Model.LoadingState.LOADED)
    val posts: LiveData<MutableList<Post>> =
        Model.instance.getAllPosts(viewModelScope, postsListLoadingState)


    private val authStateListener: FirebaseAuth.AuthStateListener = FirebaseAuth.AuthStateListener {
        if (it.currentUser != null) { // user is logged
            if (userListener != null) {
                userListener?.remove()
                userListener = null
            }

            userListener = Model.instance.getFireBaseModel()
                .addUserListener(
                    onUser = { user ->
                        _userLoadingState.postValue(Model.LoadingState.LOADED)
                        currentUser.postValue(user)
                    },
                    onError = { error ->
                        _userLoadingState.postValue(Model.LoadingState.LOADED)
                        exceptions.postValue(error)
                    })
        } else { // remove the user listener (logged out)
            if (userListener != null) {
                userListener?.remove()
                userListener = null
            }
        }
    }


    init {
        _userLoadingState.postValue(Model.LoadingState.LOADING)
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
    }

    fun refreshPosts() {
        Model.instance.refreshAllPosts(viewModelScope, postsListLoadingState)
    }

    fun addPost(post: Post, callback: () -> Unit) {
        Model.instance.addPost(post, viewModelScope, postsListLoadingState, callback)
    }

    fun updatePost(
        postUid: String,
        name: String,
        description: String,
        uri: String,
        callback: () -> Unit,
    ) {
        Model.instance.updatePost(
            postUid,
            name,
            description,
            uri,
            viewModelScope,
            postsListLoadingState,
            callback
        )
    }

    fun completeGoal(goal: Goal) {
        val currentUserGoals = currentUser.value?.goals ?: mutableListOf()
        Model.instance.goalRepository.completeGoal(currentUserGoals, goal)
    }

    fun removeGoal(goal: Goal) {
        val currentUserGoals = currentUser.value?.goals ?: mutableListOf()
        Model.instance.goalRepository.removeGoal(currentUserGoals, goal)
    }

    fun publishGoal(goal: Goal) {
        val currentUserGoals = currentUser.value?.goals ?: mutableListOf()
        Model.instance.goalRepository.publishGoal(currentUserGoals, goal)
    }

    fun addGoal(tip: Tip) {
        val currentUserGoals = currentUser.value?.goals ?: mutableListOf()
        Model.instance.goalRepository.addGoal(currentUserGoals, tip)
    }


    override fun onCleared() {
        super.onCleared()
        userListener?.remove()
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener)
    }

}