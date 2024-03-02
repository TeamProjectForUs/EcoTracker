package com.example.greenapp.Modules.Posts

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.greenapp.Model.Model
import com.example.greenapp.Model.Post

class PostsViewModel: ViewModel() {

    var posts: LiveData<MutableList<Post>>? = Model.instance.getAllPosts()
    var currentpost :LiveData<MutableList<Post>>?=null


}