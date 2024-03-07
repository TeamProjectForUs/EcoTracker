package com.example.greenapp.modules.Profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.greenapp.models.Goal
import com.example.greenapp.models.Model
import com.example.greenapp.models.MyTip
import com.example.greenapp.models.Post
import com.example.greenapp.models.Tip
import com.example.greenapp.models.User
import com.example.greenapp.modules.Tips.TipsGoalsRepository
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel(), TipsGoalsRepository {


    val myPostsLoadingState: MutableLiveData<Model.LoadingState> =
        MutableLiveData(Model.LoadingState.LOADED)

    val myTipsLoadingState: MutableLiveData<Model.LoadingState> =
        MutableLiveData(Model.LoadingState.LOADED)

    private val _myPosts: MutableLiveData<List<Post>> =
        Model.instance.postRepository.getMyPosts(myPostsLoadingState)
    val myPosts = _myPosts.map { list -> list.sortedBy { post -> post.datePosted }.reversed() }
    val myPhotos: LiveData<List<String>>
        get() = myPosts.map {
            it.map { post -> post.uri }
        }

    var myTips: LiveData<List<MyTip>> = MutableLiveData()
    override fun likeTip(
        userLikeList: MutableList<String>,
        tip: Tip,
    ) {
        Model.instance.tipsRepository.toggleTipLike(
            viewModelScope,
            MyTip.fromTip(tip),
            userLikeList
        )
    }

    override fun toggleGoalTip(userGoalsList: MutableList<Goal>, tip: Tip) {
        Model.instance.tipsRepository.toggleGoalTip(userGoalsList, tip)
    }

    override fun dislikeTip(
        userDislikeList: MutableList<String>,
        tip: Tip,
    ) {
        Model.instance.tipsRepository.dislikeTip(tip, userDislikeList)
    }

    override fun undoDislikeTip(
        userDislikeList: MutableList<String>,
        tip: Tip,
    ) {
        Model.instance.tipsRepository.undoDislikeTip(tip, userDislikeList)
    }


    fun startListeningMyTips(
        currentUserLikes: MutableList<String>,
    ) {
        myTips = Model.instance.tipsRepository.myTips(
            viewModelScope,
            currentUserLikes,
            myTipsLoadingState
        )
    }

    fun removeMyPost(post: Post) {
        Model.instance.postRepository.removeMyPost(
            viewModelScope,
            myPostsLoadingState,
            _myPosts,
            post
        )
    }

    fun publishGoal(
        user: User,
        goal: Goal,
        callback: () -> Unit,
    ) {
        Model.instance.goalRepository.publishGoal(
            viewModelScope,
            myPostsLoadingState,
            user,
            goal
        ) {
            val currentPosts = (myPosts.value ?: listOf()).toMutableList()
            currentPosts.add(it)
            _myPosts.postValue(currentPosts)
            callback()
        }
    }

    fun addMyTip(tip: MyTip) {
        Model.instance.tipsRepository.addMyTip(tip)
    }

    fun removeMyTip(tip: MyTip) {
        viewModelScope.launch {
            Model.instance.tipsRepository.removeMyTip(tip)
        }
    }


}