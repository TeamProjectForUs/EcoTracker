package com.example.greenapp.database.remote

import android.app.Activity
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import com.example.greenapp.models.Goal
import com.example.greenapp.database.Model
import com.example.greenapp.models.FriendNotification
import com.example.greenapp.models.MyTip
import com.example.greenapp.models.OtherUser
import com.example.greenapp.models.Post
import com.example.greenapp.models.Tip
import com.example.greenapp.models.User
import com.example.greenapp.models.User.Companion.FRIEND_REQUESTS_KEY

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.lang.Exception


class FirebaseManager {
    private var dbimage: StorageReference
    private val db = Firebase.firestore
    private var auth: FirebaseAuth
    private var firebaseref: DatabaseReference

    companion object {
        const val USERS_COLLECTION_PATH = "users"
        const val POSTS_COLLECTION_PATH = "posts"
        const val TIPS_COLLECTION_PATH = "tips"
        const val FAVORITE_TIPS = "favorite_tips"
    }

    fun update5TipsToLatest() {
        val ts = System.currentTimeMillis()
        db.collection(TIPS_COLLECTION_PATH)
            .limit(5)
            .get()
            .addOnSuccessListener {
                it.documents.forEach { doc->
                    doc.reference.update("addedAt", ts)
                    doc.reference.update(FieldPath.of("json","addedAt"), ts)
                }
            }
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
        callback: (List<MyTip>) -> Unit,
    ) {
        db.collection(TIPS_COLLECTION_PATH)
            .get()
            .addOnSuccessListener {
                callback(
                    it.toObjects(MyTip::class.java)
                        .filter { tip -> currentLikeList.contains(tip.id) })
            }
    }

    fun getAllTips(
        tipsLoadingState: MutableLiveData<Model.LoadingState>,
        callback: (List<Tip>) -> Unit,
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
                        callback(tips)
                    }

                    false -> callback(listOf())
                }
                tipsLoadingState.postValue(Model.LoadingState.LOADED)
            }
    }

    fun getAllUsers(callback: (List<OtherUser>) -> Unit) {

        db.collection(USERS_COLLECTION_PATH)
            .get()
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val users: MutableList<OtherUser> = mutableListOf()
                        for (document in it.result)
                            users.add(document.toObject(OtherUser::class.java))
                        callback(users)
                    }

                    false -> callback(listOf())
                }
            }
    }

    fun addUserListener(
        onUser: (User) -> Unit,
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
                    val user = snapshot.toObject(User::class.java)
                    if (user == null) {
                        onError(Exception("User document not found"))
                    } else {
                        onUser(user)
                    }
                }
            }
    }

    fun updateUser(
        name: String?,
        bio: String?,
        uri: String?, callback: () -> Unit,
        callBackWithImage: (String?) -> Unit
    ) {
        val user = Firebase.auth.currentUser
        val mapOfOtherThingsButImage = mutableMapOf<String, Any>()
        if (name != null)
            mapOfOtherThingsButImage[User.NAME_KEY] = name
        if (bio != null)
            mapOfOtherThingsButImage[User.BIO_KEY] = bio

        if (user != null) {

            uri?.let { uri ->
                dbimage.child(user.uid)
                    .putFile(uri.toUri())
                    .addOnSuccessListener { task ->
                        task.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                            callBackWithImage(it.toString())
                            db.collection(POSTS_COLLECTION_PATH)
                                .whereEqualTo(Post.USERID_KEY, user.uid)
                                .get()
                                .addOnSuccessListener { snap ->
                                    snap.forEach { doc ->

                                        doc.reference.update(Post.USER_URI_KEY, it.toString())
                                        if(name!=null)
                                            doc.reference.update(Post.NAME_KEY, name)
                                    }
                                }
                            if (name != null) {
                                mapOfOtherThingsButImage[User.URI_KEY] = it
                                db.collection(USERS_COLLECTION_PATH)
                                    .document(user.uid)
                                    .set(mapOfOtherThingsButImage, SetOptions.merge())
                                    .addOnCompleteListener {
                                        callback()
                                    }
                            } else {
                                db.collection(USERS_COLLECTION_PATH)
                                    .document(user.uid)
                                    .set(mapOfOtherThingsButImage, SetOptions.merge())
                                    .addOnCompleteListener {
                                        callback()
                                    }
                            }
                        }
                    }
            } ?: run {
                db.collection(USERS_COLLECTION_PATH)
                    .document(user.uid)
                    .set(mapOfOtherThingsButImage, SetOptions.merge())
                    .addOnCompleteListener {
                        callback()
                    }
                callBackWithImage(null)
                db.collection(POSTS_COLLECTION_PATH)
                    .whereEqualTo(Post.USERID_KEY, user.uid)
                    .get()
                    .addOnSuccessListener { snap ->
                        snap.forEach { doc ->
                            if(name!=null)
                                doc.reference.update(Post.NAME_KEY, name)
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
        callback: (User?, Exception?) -> Unit,
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    val user = User(
                        name,
                        auth.currentUser?.uid ?: "",
                        email,
                        password = "",
                        uri,
                        isChecked = false
                    )
                    db.collection(USERS_COLLECTION_PATH)
                        .document(user.id)
                        .set(user)
                        .addOnSuccessListener {
                            callback(user, null)
                        }

                } else {
                    callback(null, task.exception)
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
            .orderBy(
                Post.DATE_POSTED,
                com.google.firebase.firestore.Query.Direction.DESCENDING
            )
            .get()
            .addOnCompleteListener {
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
        description: String,
        uri: String,
        callback: () -> Unit,
    ) {
        db.collection(POSTS_COLLECTION_PATH).document(postUid).set(
            mapOf(
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

    fun removePost(post: Post, callback: () -> Unit) {
        db.collection(POSTS_COLLECTION_PATH)
            .document(post.postUid)
            .delete()
            .addOnCompleteListener {
                callback()
            }
    }

    fun getPosts(
        userId: String,
        liveData: MutableLiveData<List<Post>>,
        loadingState: MutableLiveData<Model.LoadingState>,
    ) {
        // Build a query to filter posts by username

        val query = db.collection(POSTS_COLLECTION_PATH)
            .whereEqualTo(Post.USERID_KEY, userId)
            .orderBy(Post.DATE_POSTED, com.google.firebase.firestore.Query.Direction.DESCENDING)

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
        val query = db.collection(USERS_COLLECTION_PATH)
            .whereEqualTo("name", name)
        query.get()
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val users: MutableList<User> = mutableListOf()
                        for (json in it.result) {
                            users.add(json.toObject(User::class.java))
                        }
                        callback(users)
                    }

                    false -> callback(listOf())
                }
            }
    }

    fun addMyTip(tip: MyTip) {
        val userId: String = auth.currentUser!!.uid
        db.collection(USERS_COLLECTION_PATH)
            .document(userId)
            .collection(FAVORITE_TIPS)
            .add(tip)

    }

    fun removeMyTip(tip: MyTip) {
        val userId: String = auth.currentUser!!.uid

        db.collection(USERS_COLLECTION_PATH)
            .document(userId)
            .collection(FAVORITE_TIPS)
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
            .update(User.LIKE_KEY, currentLikeList)
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

    fun toggleFriend(
        someOtherUser: User,
        userFriendList: MutableList<String>,
    ) {
        val userId: String = auth.currentUser!!.uid

        if (userFriendList.find { friendId -> friendId == someOtherUser.id } != null) {
            userFriendList.removeIf { friendId -> friendId == someOtherUser.id }
        } else {
            userFriendList.add(someOtherUser.id)
        }

        someOtherUser.friendNotifications.add(FriendNotification(userId, false))

        db.collection(USERS_COLLECTION_PATH)
            .document(someOtherUser.id)
            .update(FRIEND_REQUESTS_KEY, someOtherUser.friendNotifications)

        db.collection(USERS_COLLECTION_PATH)
            .document(userId)
            .update(User.FRIENDS_KEY, userFriendList)
    }

    fun seeFriendNotifications(
        userFriendNotifications: MutableList<FriendNotification>,
    ) {
        val userId: String = auth.currentUser!!.uid
        db.collection(USERS_COLLECTION_PATH)
            .document(userId)
            .update(FRIEND_REQUESTS_KEY, userFriendNotifications.map { notification ->
                notification.seen = true
                return@map notification
            })
    }

    fun removeFriendNotification(
        userFriendNotifications: MutableList<FriendNotification>,
        otherUserId: String,
    ) {
        val userId: String = auth.currentUser!!.uid
        userFriendNotifications.removeIf { notification ->
            notification.friendId == otherUserId
        }
        db.collection(USERS_COLLECTION_PATH)
            .document(userId)
            .update(FRIEND_REQUESTS_KEY, userFriendNotifications)
    }


    fun toggleGoalTip(tip: Tip, userGoalsList: MutableList<Goal>) {
        val userId: String = auth.currentUser!!.uid

        if (userGoalsList.find { goal -> goal.tip.id == tip.id } != null) {
            userGoalsList.removeIf { goal -> goal.tip.id == tip.id }
        } else {
            userGoalsList.add(Goal(tip.id, tip, false))
        }

        db.collection(USERS_COLLECTION_PATH)
            .document(userId)
            .update(User.GOALS_KEY, userGoalsList)
    }


}







