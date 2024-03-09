package com.example.greenapp.database

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.greenapp.database.local.AppLocalDatabase
import com.example.greenapp.database.remote.FirebaseManager
import com.example.greenapp.models.FriendNotification
import com.example.greenapp.models.Goal
import com.example.greenapp.models.MyTip
import com.example.greenapp.models.OtherUser
import com.example.greenapp.models.Post
import com.example.greenapp.models.Tip
import com.example.greenapp.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Model private constructor() {

    enum class LoadingState {
        LOADING,
        LOADED
    }

    private val localDatabase = AppLocalDatabase.db
    private val firebaseManager = FirebaseManager()


    fun getFireBaseModel(): FirebaseManager {
        return this.firebaseManager
    }

    companion object {
        val instance: Model = Model()
    }

    // STATELESS REPOSITORIES
    val userRepository = UserRepository()
    val postRepository = PostRepository()
    val goalRepository = GoalRepository()
    val tipsRepository = TipsRepository()
    val friendsRepository = FriendsRepository()

    inner class UserRepository {
        fun getAllUsersLive(coroutineScope: CoroutineScope): LiveData<List<OtherUser>> {
            firebaseManager.getAllUsers {
                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        localDatabase.otherUsersDao().insert(it)
                    }
                }
            }
            return localDatabase.otherUsersDao().getAllOtherUsersLive()
        }

        fun getAllUsers(
            coroutineScope: CoroutineScope,
            callback: (List<OtherUser>) -> Unit,
        ) {
            firebaseManager.getAllUsers {
                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        if (it.isNotEmpty()) {
                            callback(it)
                            localDatabase.otherUsersDao().insert(it)
                        } else {
                            callback(localDatabase.otherUsersDao().getAllOtherUsers())
                        }
                    }
                }
            }
        }


        fun getCurrentUser(): LiveData<User?> {
            return localDatabase.usersDao().getCurrent()
        }

        fun cacheUser(
            coroutineScope: CoroutineScope,
            user: User,
        ) {
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    localDatabase.usersDao().insert(user)
                }
            }
        }

        fun deleteCurrentUserCache(
            coroutineScope: CoroutineScope,
        ) {
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    localDatabase.usersDao().deleteAllUsers()
                }
            }
        }

        fun addUser(
            coroutineScope: CoroutineScope,
            name: String,
            email: String,
            password: String,
            uri: String,
            activity: Activity,
            callback: (User?) -> Unit,
        ) {
            firebaseManager.addUser(name, email, password, uri, activity) {
                it?.let { user ->
                    cacheUser(coroutineScope, user)
                }
                callback(it)
            }
        }

        fun updateUser(
            name: String?,
            bio: String?,
            uri: String?,
            callback: () -> Unit,
        ) {
            firebaseManager.updateUser(name, bio, uri) {
                callback()
            }
        }

        fun login(
            email: String,
            password: String,
            activity: Activity,
            callback: (Boolean) -> Unit,
        ) {
            firebaseManager.login(email, password, activity, callback)
        }

        fun signOut(coroutineScope: CoroutineScope) {
            deleteCurrentUserCache(coroutineScope)
            firebaseManager.signOut()
        }

    }

    inner class TipsRepository {

        fun getAllTips(
            coroutineScope: CoroutineScope,
            tipsLoadingState: MutableLiveData<LoadingState>,
        ): LiveData<List<Tip>> {
            firebaseManager.getAllTips(tipsLoadingState) {
                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        localDatabase.tipsDao().addTips(it)
                    }
                }
            }
            return localDatabase.tipsDao().getAllTips()
        }

        fun toggleTipLike(
            coroutineScope: CoroutineScope,
            tip: MyTip,
            currentLikeList: MutableList<String>,
        ) {
            if (currentLikeList.contains(tip.id)) {
                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        localDatabase.myTipsDao().deleteMyTip(tip)
                    }
                }
            } else {
                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        localDatabase.myTipsDao().addMyTip(tip)
                    }
                }
            }
            firebaseManager.toggleTipLike(tip, currentLikeList)
        }


        fun dislikeTip(
            tip: Tip,
            currentDislikeList: MutableList<String>,
        ) {
            firebaseManager.dislikeTip(tip, currentDislikeList)
        }

        fun undoDislikeTip(
            tip: Tip,
            currentDislikeList: MutableList<String>,
        ) {
            firebaseManager.undoDislikeTip(tip, currentDislikeList)
        }

        fun refreshAllTips(
            coroutineScope: CoroutineScope,
            tipsLoadingState: MutableLiveData<LoadingState>,
        ) {
            firebaseManager.getAllTips(tipsLoadingState) {
                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        localDatabase.tipsDao().addTips(it)
                    }
                }
            }
        }

        fun myTips(
            coroutineScope: CoroutineScope,
            currentLikeList: MutableList<String>,
            tipsLoadingState: MutableLiveData<LoadingState>,
        ): LiveData<List<MyTip>> {
            tipsLoadingState.postValue(LoadingState.LOADING)
            val lastUpdated: Long = Tip.lastUpdated
            firebaseManager.getMyTips(currentLikeList) {
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
                        localDatabase.myTipsDao().addMyTips(it)
                        // 4. Update local data
                        Tip.lastUpdated = time
                        tipsLoadingState.postValue(LoadingState.LOADED)
                    }
                }
            }
            return localDatabase.myTipsDao().getMyTips()
        }


        fun addMyTip(tip: MyTip) {
            firebaseManager.addMyTip(tip)
            localDatabase.myTipsDao().addMyTip(tip)
        }

        suspend fun removeMyTip(tip: MyTip) {
            firebaseManager.removeMyTip(tip)
            localDatabase.myTipsDao().deleteMyTip(tip)
        }


        fun toggleGoalTip(
            userGoalsList: MutableList<Goal>,
            tip: Tip,
        ) {
            firebaseManager.toggleGoalTip(tip, userGoalsList)
        }
    }


    inner class FriendsRepository {
        fun toggleFriend(
            someOtherUser: User,
            userFriendList: MutableList<String>,
        ) {
            firebaseManager.toggleFriend(someOtherUser, userFriendList)
        }

        fun removeFriendNotification(
            currentUserFriendNotifications: MutableList<FriendNotification>,
            otherUserId: String,
        ) {
            firebaseManager.removeFriendNotification(
                currentUserFriendNotifications,
                otherUserId
            )
        }

        fun seeFriendNotifications(
            currentUserFriendRequests: MutableList<FriendNotification>,
        ) {
            firebaseManager.seeFriendNotifications(
                currentUserFriendRequests,
            )
        }
    }

    inner class PostRepository {

        fun getAllPosts(
            coroutineScope: CoroutineScope,
            postsLoadingState: MutableLiveData<LoadingState>,
        ): LiveData<MutableList<Post>> {
            refreshAllPosts(coroutineScope, postsLoadingState)
            return localDatabase.postDao().getAll()
        }

        fun refreshAllPosts(
            coroutineScope: CoroutineScope,
            postsLoadingState: MutableLiveData<LoadingState>,
        ) {
            postsLoadingState.postValue(LoadingState.LOADING)

            val lastUpdated: Long = Post.lastUpdated

            firebaseManager.getAllPosts(lastUpdated) { list ->
                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        var time = lastUpdated
                        for (post in list) {
                            localDatabase.postDao().insert(post)

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
            description: String,
            uri: String,
            coroutineScope: CoroutineScope,
            postsLoadingState: MutableLiveData<LoadingState>,
            callback: () -> Unit,
        ) {
            firebaseManager.updatePost(postUid, description, uri) {
                refreshAllPosts(coroutineScope, postsLoadingState)
                callback()
            }
        }

        fun getMyPosts(
            loadingState: MutableLiveData<LoadingState>,
        ): MutableLiveData<List<Post>> {
            val liveData = MutableLiveData<List<Post>>()
            firebaseManager.getMyPosts(liveData, loadingState)
            return liveData
        }


        fun addPost(
            post: Post,
            coroutineScope: CoroutineScope,
            postsLoadingState: MutableLiveData<LoadingState>,
            callback: (Post) -> Unit,
        ) {

            firebaseManager.addPost(post) {
                refreshAllPosts(coroutineScope, postsLoadingState)
                callback(post)
            }
        }

        fun getPostById(postUid: String): LiveData<MutableList<Post>> {
            return localDatabase.postDao().getPostById(postUid)
        }

        fun removeMyPost(
            coroutineScope: CoroutineScope,
            myPostsLoadingState: MutableLiveData<LoadingState>,
            myPostsLiveData: MutableLiveData<List<Post>>,
            post: Post,
        ) {
            myPostsLoadingState.value = LoadingState.LOADING
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    localDatabase.postDao().delete(post)
                }
            }
            firebaseManager.removePost(post) {
                myPostsLoadingState.value = LoadingState.LOADED
                val mList = mutableListOf<Post>()
                myPostsLiveData.value?.let {
                    mList.addAll(it.filter { p -> p.postUid != post.postUid })
                }
                myPostsLiveData.postValue(mList)
            }
        }

    }


    inner class GoalRepository {
        fun addGoal(
            coroutineScope: CoroutineScope,
            currentGoals: MutableList<Goal>,
            tip: Tip,
        ) {
            val db = getFireBaseModel().getDB()
            val auth = getFireBaseModel().getAuth()
            val goal = Goal(tip.id, tip, false)
            currentGoals.add(goal)
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    localDatabase.goalsDao().addGoal(goal)
                }
            }
            val userId = auth.currentUser!!.uid
            db.collection(FirebaseManager.USERS_COLLECTION_PATH)
                .document(userId)
                .update("goals", currentGoals)
        }

        fun completeGoal(
            coroutineScope: CoroutineScope,
            currentGoals: MutableList<Goal>,
            goal: Goal,
        ) {
            val db = getFireBaseModel().getDB()
            val auth = getFireBaseModel().getAuth()
            val index = currentGoals.indexOf(goal)
            currentGoals[index].done = true
            val userId = auth.currentUser!!.uid
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    localDatabase.goalsDao().addGoal(goal)
                }
            }
            db.collection(FirebaseManager.USERS_COLLECTION_PATH)
                .document(userId)
                .update("goals", currentGoals)
        }

        fun removeGoal(
            coroutineScope: CoroutineScope,
            currentGoals: MutableList<Goal>,
            goal: Goal,
        ) {
            val db = getFireBaseModel().getDB()
            val auth = getFireBaseModel().getAuth()
            currentGoals.removeIf { g -> g == goal }
            val userId = auth.currentUser!!.uid
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    instance.localDatabase.goalsDao().addGoal(goal)
                }
            }
            db.collection(FirebaseManager.USERS_COLLECTION_PATH)
                .document(userId)
                .update("goals", currentGoals)
        }

        fun publishGoal(
            coroutineScope: CoroutineScope,
            postsLoadingState: MutableLiveData<LoadingState>,
            currentUser: User,
            goal: Goal,
            callback: (Post) -> Unit,
        ) {
            val auth = getFireBaseModel().getAuth()
            val userId = auth.currentUser!!.uid
            postRepository.addPost(
                Post(
                    userId = userId,
                    name = "",
                    userUri = currentUser.getImage(),
                    description = "I have set ${goal.tip.description} as my goal!",
                    isChecked = false,
                    postUid = ""
                ), coroutineScope,
                postsLoadingState,
                callback
            )
        }
    }

}