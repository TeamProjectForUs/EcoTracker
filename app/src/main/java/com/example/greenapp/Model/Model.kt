package com.example.greenapp.Model

import android.app.Activity
import android.os.Looper
import android.util.Log
import androidx.core.os.HandlerCompat
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.greenapp.dao.AppLocalDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

class Model private constructor() {

    enum class LoadingState {
        LOADING,
        LOADED
    }

    private val database = AppLocalDatabase.db
    private val firebaseModel = FirebaseModel()
    val goalRepository get() = GoalRepository()

    fun getFireBaseModel(): FirebaseModel {
        return this.firebaseModel
    }

    companion object {
        val instance: Model = Model()
    }

    fun getAllUsers(callback: (List<User>) -> Unit) {
        firebaseModel.getAllUsers(callback)
    }


    fun addUser(
        name: String,
        email: String,
        password: String,
        uri: String,
        activity: Activity,
        callback: (Boolean) -> Unit,
    ) {
        firebaseModel.addUser(name, email, password, uri, activity, callback)
    }

    fun updateUser(name: String, uri: String, callback: () -> Unit) {
        firebaseModel.updateUser(name, uri) {
            callback()
        }
    }

    fun login(email: String, password: String, activity: Activity, callback: (Boolean) -> Unit) {
        firebaseModel.login(email, password, activity, callback)
    }

    fun signOut() {
        firebaseModel.signOut()
    }

    fun getUserByName(name: String, callback: (List<User>?) -> Unit) {
        firebaseModel.getUserByName(name, callback)
    }

    fun getAllTips(
        tipsLoadingState: MutableLiveData<LoadingState>,
    ): MutableLiveData<List<Tip>> {
        val tipsLiveData = MutableLiveData<List<Tip>>()
        firebaseModel.getAllTips(tipsLoadingState, tipsLiveData)
        return tipsLiveData
    }

    fun toggleTipLike(
        coroutineScope: CoroutineScope,
        tip: Tip,
        currentLikeList: MutableList<String>,
    ) {
        if (currentLikeList.contains(tip.id)) {
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    database.tipsDao().deleteTip(tip)
                }
            }
        } else {
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    database.tipsDao().addTip(tip)
                }
            }
        }
        firebaseModel.toggleTipLike(tip, currentLikeList)
    }


    fun dislikeTip(
        tip: Tip,
        currentDislikeList: MutableList<String>,
    ) {
        firebaseModel.dislikeTip(tip, currentDislikeList)
    }

    fun undoDislikeTip(
        tip: Tip,
        currentDislikeList: MutableList<String>,
    ) {
        firebaseModel.undoDislikeTip(tip, currentDislikeList)
    }

    fun refreshAllTips(
        tipsLoadingState: MutableLiveData<LoadingState>,
        tipsLiveData: MutableLiveData<List<Tip>>,
    ) {
        firebaseModel.getAllTips(tipsLoadingState, tipsLiveData)
    }

    fun myTips(
        coroutineScope: CoroutineScope,
        currentLikeList: MutableList<String>,
        tipsLoadingState: MutableLiveData<LoadingState>,
    ): LiveData<List<Tip>> {
        tipsLoadingState.postValue(LoadingState.LOADING)
        val lastUpdated: Long = Tip.lastUpdated
        firebaseModel.getMyTips(currentLikeList) {
            Log.i("TAG", "Firebase returned tips ${it.size}, lastUpdated: $lastUpdated")
            // 3. Insert new record to ROOM
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    var time = lastUpdated
                    for (tip in it) {
                        tip.lastUpdated.let { updated ->
                            if (time < updated)
                                time = tip.lastUpdated
                        }
                    }
                    database.tipsDao().addTips(it)
                    // 4. Update local data
                    Tip.lastUpdated = time
                    tipsLoadingState.postValue(LoadingState.LOADED)
                }
            }
        }
        return database.tipsDao().getMyTips()
    }

    fun getAllPosts(
        coroutineScope: CoroutineScope,
        postsLoadingState: MutableLiveData<LoadingState>,
    ): LiveData<MutableList<Post>> {
        refreshAllPosts(coroutineScope, postsLoadingState)
        return database.postDao().getAll()
    }

    fun refreshAllPosts(
        coroutineScope: CoroutineScope,
        postsLoadingState: MutableLiveData<LoadingState>,
    ) {
        postsLoadingState.postValue(LoadingState.LOADING)

        // 1. Get last local update
        val lastUpdated: Long = Post.lastUpdated

        // 2. Get all updated records from firestore since last update locally
        firebaseModel.getAllPosts(lastUpdated) { list ->
            Log.i("TAG", "Firebase returned ${list.size}, lastUpdated: $lastUpdated")
            // 3. Insert new record to ROOM
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    var time = lastUpdated
                    for (post in list) {
                        database.postDao().insert(post)

                        post.lastUpdated?.let {
                            if (time < it)
                                time = post.lastUpdated ?: System.currentTimeMillis()
                        }
                    }
                    // 4. Update local data
                    Post.lastUpdated = time
                    postsLoadingState.postValue(LoadingState.LOADED)
                }
            }
        }

    }


    fun updatePost(
        postUid: String,
        name: String,
        description: String,
        uri: String,
        coroutineScope: CoroutineScope,
        postsLoadingState: MutableLiveData<LoadingState>,
        callback: () -> Unit,
    ) {
        firebaseModel.updatePost(postUid, name, description, uri) {
            refreshAllPosts(coroutineScope, postsLoadingState)
            callback()
        }

    }

    fun getMyPosts(
        loadingState: MutableLiveData<LoadingState>,
    ): MutableLiveData<List<Post>> {
        val liveData = MutableLiveData<List<Post>>()
        firebaseModel.getMyPosts(liveData, loadingState)
        return liveData
    }

    fun addMyTip(tip: Tip) {
        firebaseModel.addMyTip(tip)
        database.tipsDao().addTip(tip)
    }

    suspend fun removeMyTip(tip: Tip) {
        firebaseModel.removeMyTip(tip)
        database.tipsDao().deleteTip(tip)
    }

    fun addPost(
        post: Post,
        coroutineScope: CoroutineScope,
        postsLoadingState: MutableLiveData<LoadingState>,
        callback: () -> Unit,
    ) {
        firebaseModel.addPost(post) {
            refreshAllPosts(coroutineScope, postsLoadingState)
            callback()
        }
    }

    fun getPostById(postUid: String): LiveData<MutableList<Post>> {
        return database.postDao().getPostById(postUid)
    }

    fun toggleGoalTip(
        userGoalsList: MutableList<Goal>,
        tip: Tip,
    ) {
        firebaseModel.toggleGoalTip(tip, userGoalsList)
    }

}