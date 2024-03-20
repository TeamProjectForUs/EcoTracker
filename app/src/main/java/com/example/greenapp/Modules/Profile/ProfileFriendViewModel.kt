package com.example.greenapp.modules.Profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.greenapp.database.Model
import com.example.greenapp.models.Post

class ProfileFriendViewModel(otherUserId: String) : ViewModel() {

    val userPostsLoadingState: MutableLiveData<Model.LoadingState> =
        MutableLiveData(Model.LoadingState.LOADED)
    private val _posts: MutableLiveData<List<Post>> =
        Model.instance.postRepository.getUserPosts(otherUserId, userPostsLoadingState)
    val posts: LiveData<List<Post>> get() = _posts


    class ProfileFriendViewModelFactory(private val otherUserId: String) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfileFriendViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ProfileFriendViewModel(otherUserId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}