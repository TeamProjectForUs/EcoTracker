package com.example.greenapp.Modules

import android.view.Display.Mode
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.greenapp.Model.Model
import com.example.greenapp.Model.Post
import com.example.greenapp.Model.Tip
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _myPosts: MutableLiveData<List<Post>> = MutableLiveData()
    val myPosts: LiveData<List<Post>> get() = MutableLiveData()

    val myTips: LiveData<List<Tip>> get() = Model.instance.myTips()

    fun addMyTip(tip: Tip) {
        Model.instance.addMyTip(tip)
    }

    fun removeMyTip(tip: Tip) {
        viewModelScope.launch {
            Model.instance.removeMyTip(tip)
        }
    }

    val myPhotos: LiveData<List<String>>
        get() = _myPosts.map {
            it.map { post -> post.uri }
        }

    init {
        Model.instance.getMyPosts {
            _myPosts.postValue(it)
        }

    }
}