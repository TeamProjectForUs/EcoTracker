package com.example.greenapp.Modules.Profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.greenapp.Model.Goal
import com.example.greenapp.Model.Model
import com.example.greenapp.Model.Post
import com.example.greenapp.Model.Tip
import com.example.greenapp.Modules.Tips.TipsGoalsRepository
import com.example.greenapp.Modules.Tips.TipsRepository
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel(), TipsGoalsRepository {


    val myPostsLoadingState: MutableLiveData<Model.LoadingState> =
        MutableLiveData(Model.LoadingState.LOADED)

    val myTipsLoadingState: MutableLiveData<Model.LoadingState> =
        MutableLiveData(Model.LoadingState.LOADED)

    val myPosts: LiveData<List<Post>> = Model.instance.getMyPosts(myPostsLoadingState)

    override fun likeTip(
        userLikeList: MutableList<String>,
        tip: Tip,
    ) {
        Model.instance.toggleTipLike(viewModelScope, tip, userLikeList)
    }

    override fun toggleGoalTip(userGoalsList: MutableList<Goal>, tip: Tip) {
        Model.instance.toggleGoalTip(userGoalsList, tip)
    }

    override fun dislikeTip(
        userDislikeList: MutableList<String>,
        tip: Tip,
    ) {
        Model.instance.dislikeTip(tip, userDislikeList)
    }

    override fun undoDislikeTip(
        userDislikeList: MutableList<String>,
        tip: Tip,
    ) {
        Model.instance.undoDislikeTip(tip, userDislikeList)
    }


    val myPhotos: LiveData<List<String>>
        get() = myPosts.map {
            it.map { post -> post.uri }
        }

    var myTips: LiveData<List<Tip>> = MutableLiveData()

    fun startListeningMyTips(
        currentUserLikes: MutableList<String>,
    ) {
        myTips = Model.instance.myTips(
            viewModelScope,
            currentUserLikes,
            myTipsLoadingState
        )
    }

    fun addMyTip(tip: Tip) {
        Model.instance.addMyTip(tip)
    }

    fun removeMyTip(tip: Tip) {
        viewModelScope.launch {
            Model.instance.removeMyTip(tip)
        }
    }


}