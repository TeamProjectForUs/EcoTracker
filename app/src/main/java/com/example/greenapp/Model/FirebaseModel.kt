package com.example.greenapp.Model

import android.app.Activity
import android.content.ContentValues.TAG
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.firestore.persistentCacheSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import java.lang.Exception
import java.util.UUID


class FirebaseModel {
    private var dbimage: StorageReference
    private val db = Firebase.firestore
    private var auth: FirebaseAuth
    private lateinit var firebaseref: DatabaseReference

    companion object {
        const val USERS_COLLECTION_PATH = "users"
        const val POSTS_COLLECTION_PATH = "posts"
        const val TIPS_COLLECTION_PATH = "tips"
    }

    fun getDB(): FirebaseFirestore {
        return db
    }

    fun getAuth(): FirebaseAuth {
        return auth
    }

    init {
        val settings = firestoreSettings {
            setLocalCacheSettings(memoryCacheSettings { })
        }
        db.firestoreSettings = settings
        auth = Firebase.auth
        dbimage = FirebaseStorage.getInstance().getReference("images")
        firebaseref = FirebaseDatabase.getInstance().getReference("posts")

    }

    fun getMyTips(
        currentLikeList: MutableList<String>,
        callback: (List<Tip>) -> Unit,
    ) {
        db.collection(TIPS_COLLECTION_PATH)
            .get()
            .addOnSuccessListener {
                val tips: MutableList<Tip> = mutableListOf()
                for (doc in it.documents) {
                    doc.data?.let {
                        val tip = Tip.fromJSON(it)
                        tips.add(tip)
                    }
                }
                callback(tips.filter { tip -> currentLikeList.contains(tip.id) })
            }
    }

    fun getAllTips(
        tipsLoadingState: MutableLiveData<Model.LoadingState>,
        tipsLiveData: MutableLiveData<List<Tip>>,
    ) {
        db.collection(TIPS_COLLECTION_PATH)
            .get()
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val tips: MutableList<Tip> = mutableListOf()
                        for (json in it.result) {
                            val tip = Tip.fromJSON(json.data)
                            tips.add(tip)
                        }
                        tipsLiveData.postValue(tips)
                    }

                    false -> tipsLiveData.postValue(listOf())
                }
                tipsLoadingState.postValue(Model.LoadingState.LOADED)
            }
    }


    fun getAllUsers(callback: (List<User>) -> Unit) {
        db.collection(USERS_COLLECTION_PATH).get().addOnCompleteListener {
            when (it.isSuccessful) {
                true -> {
                    val users: MutableList<User> = mutableListOf()
                    for (json in it.result) {
                        val user = User.fromJSON(json.data)
                        users.add(user)
                    }
                    callback(users)
                }

                false -> callback(listOf())
            }
        }
    }

    fun addUserListener(
        onUser: (UserModelFirebase) -> Unit,
        onError: (Exception) -> Unit,
    ): ListenerRegistration {
        val userId: String = auth.currentUser!!.uid
        return db.collection(USERS_COLLECTION_PATH)
            .document(userId)
            .addSnapshotListener { snapshot, ex ->
                if (ex != null) {
                    onError(ex)
                } else if (snapshot == null) {
                    onError(Exception("User document not found"))
                } else {
                    val user = snapshot.toObject(UserModelFirebase::class.java)
                    if (user == null) {
                        onError(Exception("User document not found"))
                    } else {
                        onUser(user)
                    }
                }
            }
    }


    fun updateUser(name: String, uri: String, callback: () -> Unit) {
        val user = Firebase.auth.currentUser

        if (user != null) {

            dbimage.child(user.uid)
                .putFile(uri.toUri())
                .addOnSuccessListener { task ->
                    task.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        db.collection(USERS_COLLECTION_PATH).document(user.uid).set(
                            mapOf(
                                "name" to name,
                                "uri" to it,
                                "lastUpdated" to FieldValue.serverTimestamp()
                            ), SetOptions.merge()
                        ).addOnCompleteListener {
                            callback()
                        }
                    }
                }
        }
    }

    fun addUser(
        name: String,
        email: String,
        password: String,
        uri: String,
        activity: Activity,
        callback: (Boolean) -> Unit,
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    var id: String = "null"
                    if (auth.currentUser != null) {
                        id = auth.currentUser!!.uid
                    }
                    val user = User(name, id, email, password, uri, isChecked = false)

                    db.collection(USERS_COLLECTION_PATH)
                        .document(user.id)
                        .set(user.json)
                        .addOnSuccessListener {
                            callback(true)
                        }


                } else {
                    callback(false)
                }
            }
    }

    fun login(email: String, password: String, activity: Activity, callback: (Boolean) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    callback(true)
                    // Sign in success, update UI with the signed-in user's information
                } else {
                    callback(false)
                    // Toast.makeText(context, "Failed to login user.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun signOut() {
        auth.signOut()
    }


    fun getAllPosts(since: Long, callback: (List<Post>) -> Unit) {
        db.collection(POSTS_COLLECTION_PATH)
            .whereGreaterThan(Post.LAST_UPDATED, Timestamp(since, 0)).get().addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val posts: MutableList<Post> = mutableListOf()
                        for (json in it.result) {
                            val post = Post.fromJSON(json.data)
                            posts.add(post)
                        }
                        callback(posts)
                    }

                    false -> callback(listOf())
                }
            }
    }

    fun updatePost(
        postUid: String,
        name: String,
        description: String,
        uri: String,
        callback: () -> Unit,
    ) {
        db.collection(POSTS_COLLECTION_PATH).document(postUid).set(
            mapOf(
                "name" to name,
                "description" to description,
                "uri" to uri,
                "lastUpdated" to FieldValue.serverTimestamp()

            ), SetOptions.merge()
        ).addOnCompleteListener { callback() }
    }

    fun addPost(post: Post, callback: () -> Unit) {
        // post.postUid= postUid.toString()
        post.postUid = firebaseref.push().key!!

        if (post.isDefaultImage()) {
            db.collection(POSTS_COLLECTION_PATH)
                .document(post.postUid).set(post.json)
                .addOnSuccessListener {
                    callback()
                }.addOnFailureListener { e -> e.printStackTrace() }
        } else {
            dbimage.child(post.postUid)
                .putFile(post.uri.toUri())
                .addOnSuccessListener { task ->
                    task.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener {
                            post.uri = it.toString()
                            db.collection(POSTS_COLLECTION_PATH)
                                .document(post.postUid).set(post.json)
                                .addOnSuccessListener {
                                    callback()
                                }.addOnFailureListener { e -> e.printStackTrace() }
                        }
                }.addOnFailureListener { e -> e.printStackTrace() }
        }

    }


    fun getMyPosts(
        liveData: MutableLiveData<List<Post>>,
        loadingState: MutableLiveData<Model.LoadingState>,
    ) {
        // Build a query to filter posts by username

        val query = db.collection(POSTS_COLLECTION_PATH)
            .whereEqualTo("id", auth.currentUser?.uid)

        query.get().addOnCompleteListener {
            when (it.isSuccessful) {
                true -> {
                    val posts: MutableList<Post> = mutableListOf()
                    for (json in it.result) {
                        val post = Post.fromJSON(json.data)
                        posts.add(post)
                    }
                    loadingState.postValue(Model.LoadingState.LOADED)
                    liveData.postValue(posts)
                }

                false -> {
                    liveData.postValue(listOf())
                    loadingState.postValue(Model.LoadingState.LOADED)
                }
            }
        }
    }

    fun getUserByName(name: String, callback: (List<User>?) -> Unit) {
        // Build a query to filter posts by username
        val query = db.collection(USERS_COLLECTION_PATH)
            .whereEqualTo("name", name) // Assuming "username" is the field containing the name

        query.get().addOnCompleteListener {
            when (it.isSuccessful) {
                true -> {
                    val users: MutableList<User> = mutableListOf()
                    for (json in it.result) {
                        val user = User.fromJSON(json.data)
                        users.add(user)
                    }
                    callback(users)
                }

                false -> callback(listOf())
            }
        }
    }

    fun addMyTip(tip: Tip) {
        val userId: String = auth.currentUser!!.uid

        db.collection(USERS_COLLECTION_PATH)
            .document(userId)
            .collection("favorite_tips")
            .add(tip)

    }

    fun removeMyTip(tip: Tip) {
        val userId: String = auth.currentUser!!.uid

        db.collection(USERS_COLLECTION_PATH)
            .document(userId)
            .collection("favorite_tips")
            .document(tip.id)
            .delete()

    }

    fun dislikeTip(
        tip: Tip,
        currentDislikeList: MutableList<String>,
    ) {
        val userId: String = auth.currentUser!!.uid

        currentDislikeList.add(tip.id)
        db.collection(USERS_COLLECTION_PATH)
            .document(userId)
            .update(User.DISLIKE_LIST, currentDislikeList)

        db.collection(TIPS_COLLECTION_PATH)
            .document(tip.id)
            .update(Tip.DISLIKES, tip.dislikes + 1)

    }

    fun toggleTipLike(
        tip: Tip,
        currentLikeList: MutableList<String>,
    ) {
        val userId: String = auth.currentUser!!.uid
        if (currentLikeList.contains(tip.id)) {
            currentLikeList.remove(tip.id)
        } else {
            currentLikeList.add(tip.id)
        }

        db.collection(USERS_COLLECTION_PATH)
            .document(userId)
            .update(UserModelFirebase.LIKE_KEY, currentLikeList)
    }

    fun undoDislikeTip(
        tip: Tip,
        currentDislikeList: MutableList<String>,
    ) {
        val userId: String = auth.currentUser!!.uid
        currentDislikeList.remove(tip.id)
        db.collection(USERS_COLLECTION_PATH)
            .document(userId)
            .update(User.DISLIKE_LIST, currentDislikeList)


        db.collection(TIPS_COLLECTION_PATH)
            .document(tip.id)
            .update(Tip.DISLIKES, tip.dislikes - 1)

    }

    fun toggleGoalTip(tip: Tip, userGoalsList: MutableList<Goal>) {
        val userId: String = auth.currentUser!!.uid

        if (userGoalsList.find { goal -> goal.tip.id == tip.id } != null) {
            userGoalsList.removeIf { goal -> goal.tip.id == tip.id }
        } else {
            userGoalsList.add(Goal(tip, false))
        }

        db.collection(USERS_COLLECTION_PATH)
            .document(userId)
            .update(UserModelFirebase.GOALS_KEY, userGoalsList)
    }


}







