package com.example.greenapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.greenapp.models.Goal
import com.example.greenapp.databinding.GoalItemFriendBinding

class FriendGoalsAdapter(
    private var goals: List<Goal>,
) : RecyclerView.Adapter<FriendGoalsAdapter.FriendGoalsViewHolder>() {


    class FriendGoalsViewHolder(val goalItemBinding: GoalItemFriendBinding) :
        RecyclerView.ViewHolder(goalItemBinding.root)
    fun setGoalListWithChange(goals: List<Goal>, positionChanged: Int) {
        this.goals = goals
        notifyItemChanged(positionChanged)
    }

    fun setGoals(goals: List<Goal>) {
        this.goals = goals
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendGoalsViewHolder {
        val binding = GoalItemFriendBinding.inflate(LayoutInflater.from(parent.context))
        return FriendGoalsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return goals.size
    }

    override fun onBindViewHolder(holder: FriendGoalsViewHolder, position: Int) {
        val goal = goals[position]
        holder.goalItemBinding.tipDescriptionTv.text = goal.tip.description
    }
}