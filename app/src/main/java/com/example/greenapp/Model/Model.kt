package com.example.greenapp.Model

import android.app.Activity
import android.os.Looper
import android.util.Log
import androidx.core.os.HandlerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.greenapp.dao.AppLocalDatabase
import java.util.concurrent.Executors

class Model private constructor() {

    enum class LoadingState{
        LOADING,
        LOADED
    }

    private val database = AppLocalDatabase.db
    private var executor = Executors.newSingleThreadExecutor()
    private var mainHandler = HandlerCompat.createAsync(Looper.getMainLooper())
    private val firebaseModel = FirebaseModel()
    private val posts: LiveData<MutableList<Post>>? = null
    private val post:LiveData<MutableList<Post>>?=null
    val postsListLoadingState: MutableLiveData<LoadingState> = MutableLiveData(LoadingState.LOADED)


    companion object {
        val instance: Model = Model()
    }

    interface GetAllUsersListener {
        fun onComplete(users: List<User>)
    }
    interface GetAllPostsListener{
        fun onComplete(posts:List<Post>)
    }
fun getUrl(callback: (String) -> Unit){
    firebaseModel.getUrl(callback)
}
    fun getAllUsers(callback: (List<User>) -> Unit) {
        firebaseModel.getAllUsers(callback)
//        executor.execute {
//
//            Thread.sleep(5000)
//
//            val Users = database.userDao().getAll()
//            mainHandler.post {
//                // Main Thread
//                callback(Users)
//            }
//        }
    }
    fun getCurrentUser(callback: (List<String>) -> Unit){
        firebaseModel.getCurrentUser(callback)
    }

    fun addUser(name:String,email: String,password: String,uri: String,activity: Activity ,callback: (Boolean) -> Unit) {

        firebaseModel.addUser(name,email,password,uri,activity ,callback)
//        executor.execute {
//            database.userDao().insert(user)
//            mainHandler.post {
//                callback()
//            }
//        }
    }
    fun updateUser(name: String,uri: String,callback: () -> Unit){
        firebaseModel.updateUser(name,uri){
            callback()
        }
    }
    fun login(email:String,password:String,activity: Activity ,callback: (Boolean) -> Unit){
        firebaseModel.login(email,password,activity ,callback)
    }
    fun signOut(){
        firebaseModel.signOut()
    }

    fun getUserByName(name:String,callback: (List<User>?) -> Unit){
        firebaseModel.getUserByName(name,callback)
    }
    fun getAllPosts():LiveData<MutableList<Post>>{
        refreshAllPosts()
        return posts?:database.postDao().getAll()
        //firebaseModel.getAllPosts(callback)
    }

     fun refreshAllPosts() {
        postsListLoadingState.value = LoadingState.LOADING

        // 1. Get last local update
        val lastUpdated: Long = Post.lastUpdated

        // 2. Get all updated records from firestore since last update locally
        firebaseModel.getAllPosts(lastUpdated) { list ->
            Log.i("TAG", "Firebase returned ${list.size}, lastUpdated: $lastUpdated")
            // 3. Insert new record to ROOM
            executor.execute {
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
                postsListLoadingState.postValue(LoadingState.LOADED)
            }
        }

    }
    fun updatePost(postUid:String,name: String,description:String,uri:String,callback: () -> Unit){
        firebaseModel.updatePost(postUid,name,description,uri){
            refreshAllPosts()
            callback()
        }

    }

    fun getMyPosts(callback: (List<Post>?) -> Unit){
        firebaseModel.getMyPosts(callback)
    }
    fun addPost(post: Post, callback: () -> Unit){
        firebaseModel.addPost(post){
            refreshAllPosts()
            callback()
        }
    }
    fun getPostById(postUid: String): LiveData<MutableList<Post>> {
        return post?:database.postDao().getPostById(postUid)
    }


}