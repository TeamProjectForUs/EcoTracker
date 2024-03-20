package com.example.greenapp.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.greenapp.models.Goal
import com.example.greenapp.databinding.GoalItemBinding

class GoalsAdapter(
    private var goals: List<Goal>,
    private val onCompleteGoal: (Goal) -> Unit,
    private val onRemoveGoal: (Goal) -> Unit,
    private val onPublishGoal: (Goal) -> Unit,
) : RecyclerView.Adapter<GoalsAdapter.GoalsViewHolder>() {


    class GoalsViewHolder(val goalItemBinding: GoalItemBinding) :
        RecyclerView.ViewHolder(goalItemBinding.root)
    fun setGoalListWithChange(goals: List<Goal>, positionChanged: Int) {
        this.goals = goals
        notifyItemChanged(positionChanged)
    }

    fun setGoals(goals: List<Goal>) {
        this.goals = goals
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalsViewHolder {
        val binding = GoalItemBinding.inflate(LayoutInflater.from(parent.context))
        return GoalsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return goals.size
    }

    override fun onBindViewHolder(holder: GoalsViewHolder, position: Int) {
        val goal = goals[position]
        holder.goalItemBinding.tipDescriptionTv.text = goal.tip.description
        if(goal.done) {
            holder.goalItemBinding.completeGoalBtn.imageTintList = ColorStateList.valueOf(Color.GREEN)
        }

        holder.goalItemBinding.completeGoalBtn.setOnClickListener {
            onCompleteGoal.invoke(goal)
        }
        holder.goalItemBinding.removeGoalBtn.setOnClickListener {
            onRemoveGoal.invoke(goal)
        }
        holder.goalItemBinding.publishGoalBtn.setOnClickListener {
            onPublishGoal.invoke(goal)
        }
    }
}