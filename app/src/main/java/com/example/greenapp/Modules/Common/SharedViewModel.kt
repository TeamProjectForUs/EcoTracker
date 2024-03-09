package com.example.greenapp.modules.Common

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.greenapp.models.Goal
import com.example.greenapp.database.Model
import com.example.greenapp.models.FriendNotification
import com.example.greenapp.models.Post
import com.example.greenapp.models.Tip
import com.example.greenapp.models.User
import com.example.greenapp.modules.AirQuality.AirQuality
import com.example.greenapp.modules.AirQuality.AirQualityAPI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch
import java.lang.Exception


// Handles the user + posts
// Common to a lot of screens
class SharedViewModel : ViewModel() {

    private val airQualityApi = AirQualityAPI()
    val airQuality = MutableLiveData<AirQuality?>()
    val currentUser: LiveData<User?> = Model.instance.userRepository.getCurrentUser()
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
        Model.instance.postRepository.getAllPosts(viewModelScope, postsListLoadingState)

    val unseenFriendNotifications: LiveData<List<FriendNotification>> = currentUser.map {
        return@map it?.friendNotifications?.filter { friendNotification ->
            return@filter !friendNotification.seen
        } ?: listOf()
    }


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

                        Model.instance.userRepository.cacheUser(viewModelScope, user)
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
        Model.instance.postRepository.refreshAllPosts(viewModelScope, postsListLoadingState)
    }

    fun addPost(post: Post, callback: (Post) -> Unit) {
        Model.instance.postRepository.addPost(post, viewModelScope, postsListLoadingState, callback)
    }

    fun updatePost(
        postUid: String,
        description: String,
        uri: String,
        callback: () -> Unit,
    ) {
        Model.instance.postRepository.updatePost(
            postUid,
            description,
            uri,
            viewModelScope,
            postsListLoadingState,
            callback
        )
    }

    fun completeGoal(goal: Goal) {
        val currentUserGoals = currentUser.value?.goals ?: mutableListOf()
        Model.instance.goalRepository.completeGoal(viewModelScope, currentUserGoals, goal)
    }

    fun removeGoal(goal: Goal) {
        val currentUserGoals = currentUser.value?.goals ?: mutableListOf()
        Model.instance.goalRepository.removeGoal(viewModelScope, currentUserGoals, goal)
    }


    fun addGoal(tip: Tip) {
        val currentUserGoals = currentUser.value?.goals ?: mutableListOf()
        Model.instance.goalRepository.addGoal(viewModelScope, currentUserGoals, tip)
    }


    fun toggleFriend(
        someOtherUser: User,
        userFriendList: MutableList<String>,
    ) {
        Model.instance.friendsRepository.toggleFriend(someOtherUser, userFriendList)
    }

    fun removeFriendNotification(
        userId: String,
    ) {
        val currentUserFriendRequests = currentUser.value?.friendNotifications ?: mutableListOf()
        Model.instance.friendsRepository.removeFriendNotification(currentUserFriendRequests, userId)
    }

    fun seeFriendNotifications() {
        val currentUserFriendRequests = currentUser.value?.friendNotifications ?: mutableListOf()
        if (currentUserFriendRequests
                .any { notification -> !notification.seen }
        ) Model.instance.friendsRepository.seeFriendNotifications(currentUserFriendRequests)
    }

    fun getAirQuality(context: Context) {
        viewModelScope.launch {
            airQuality.postValue(airQualityApi.getCurrentPositionAirQuality(context))
        }
    }

    override fun onCleared() {
        super.onCleared()
        userListener?.remove()
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener)
    }

}