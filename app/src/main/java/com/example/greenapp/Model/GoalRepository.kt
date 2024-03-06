package com.example.greenapp.Model

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope

class GoalRepository {

    fun addGoal(
        currentGoals: MutableList<Goal>,
        tip: Tip,
    ) {
        val db = Model.instance.getFireBaseModel().getDB()
        val auth = Model.instance.getFireBaseModel().getAuth()

        currentGoals.add(Goal(tip, false))
        val userId = auth.currentUser!!.uid
        db.collection(FirebaseModel.USERS_COLLECTION_PATH)
            .document(userId)
            .update("goals", currentGoals)
    }

    fun completeGoal(
        currentGoals: MutableList<Goal>,
        goal: Goal,
    ) {
        val db = Model.instance.getFireBaseModel().getDB()
        val auth = Model.instance.getFireBaseModel().getAuth()
        val index = currentGoals.indexOf(goal)
        currentGoals[index].done = true
        val userId = auth.currentUser!!.uid
        db.collection(FirebaseModel.USERS_COLLECTION_PATH)
            .document(userId)
            .update("goals", currentGoals)
    }

    fun removeGoal(
        currentGoals: MutableList<Goal>,
        goal: Goal,
    ) {
        val db = Model.instance.getFireBaseModel().getDB()
        val auth = Model.instance.getFireBaseModel().getAuth()
        currentGoals.removeIf { g -> g == goal }
        val userId = auth.currentUser!!.uid
        db.collection(FirebaseModel.USERS_COLLECTION_PATH)
            .document(userId)
            .update("goals", currentGoals)
    }

    fun publishGoal(
        coroutineScope: CoroutineScope,
        postsLoadingState: MutableLiveData<Model.LoadingState>,
        currentUser: UserModelFirebase,
        goal: Goal,
        callback: (Post) -> Unit,
    ) {
        val auth = Model.instance.getFireBaseModel().getAuth()
        val userId = auth.currentUser!!.uid
        Model.instance.addPost(
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