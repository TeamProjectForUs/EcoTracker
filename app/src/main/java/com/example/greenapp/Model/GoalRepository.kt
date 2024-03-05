package com.example.greenapp.Model

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
        currentGoals: MutableList<Goal>,
        goal: Goal,
    ) {

    }
}