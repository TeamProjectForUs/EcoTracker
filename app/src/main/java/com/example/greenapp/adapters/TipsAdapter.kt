package com.example.greenapp.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.greenapp.Model.Goal
import com.example.greenapp.Model.Tip
import com.example.greenapp.Model.UserModelFirebase
import com.example.greenapp.R
import com.example.greenapp.databinding.TipItemBinding

class TipsAdapter(
    private var userDislikeList: List<String>,
    private var userLikeList: List<String>,
    private var userGoalList: List<Goal>,
    private var tipData: TipAdapterData,
    private val onTipDislike: (Tip, Int) -> Unit,
    private val onTipLike: (Tip, Int) -> Unit,
    private val onMakeGoal: (Tip, Int) -> Unit = { _, _ -> },
    private val isLikedPage: Boolean = false,
) : RecyclerView.Adapter<TipsAdapter.TipsViewHolder>() {

    fun setTipsData(data: TipAdapterData) {
        tipData = data
        notifyDataSetChanged()
    }

    fun updateUserDislikeList(list: List<String>, position: Int) {
        this.userDislikeList = list
        notifyItemChanged(position)
    }

    fun updateUserGoalList(list: List<Goal>, position: Int) {
        this.userGoalList = list
        notifyItemChanged(position)
    }

    fun updateUserLikeList(list: List<String>, position: Int) {
        this.userLikeList = list
        notifyItemChanged(position)
    }

    fun isAllTips(): Boolean {
        return tipData is TipAdapterData.AllTips
    }


    sealed class TipAdapterData(open val tips: List<Tip>) {
        data class NewTips(override var tips: List<Tip>) : TipAdapterData(tips)
        data class AllTips(override var tips: List<Tip>) : TipAdapterData(tips)
    }

    private var colorHighlight: Int? = null
    private var colorNormal: Int? = null


    class TipsViewHolder(binding: TipItemBinding) : RecyclerView.ViewHolder(binding.root) {

        val desc = binding.tipDescriptionTv
        val likeBtn = binding.likeTipBtn
        val dislikeBtn = binding.dislikeTipBtn
        val goalBtn = binding.makeGoalBtn

        val card = binding.tipCard


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipsViewHolder {
        val binding = TipItemBinding.inflate(LayoutInflater.from(parent.context))
        if (colorHighlight == null || colorNormal == null) {
            colorHighlight = ContextCompat.getColor(parent.context, R.color.strockGreenLight)
            colorNormal = ContextCompat.getColor(parent.context, R.color.white)
        }
        return TipsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return tipData.tips.size
    }


    override fun onBindViewHolder(holder: TipsViewHolder, position: Int) {
        val tip = tipData.tips[position]
        holder.desc.text = tip.description

        if (isLikedPage) {
            holder.dislikeBtn.visibility = GONE
        } else {
            holder.goalBtn.visibility = GONE
        }

        if (userGoalList.find { goal -> goal.tip.id == tip.id } != null) {
            holder.goalBtn.setImageResource(R.drawable.minus)
        } else {
            holder.goalBtn.setImageResource(R.drawable.plus)
        }

        holder.goalBtn.setOnClickListener {
            onMakeGoal.invoke(tip, position)
        }

        if (userLikeList.contains(tip.id)) {
            holder.likeBtn.setImageResource(R.drawable.heart_fill)
        } else {
            holder.likeBtn.setImageResource(R.drawable.favorite_fill0_wght400_grad0_opsz24)
        }

        if (userDislikeList.contains(tip.id)) {
            holder.dislikeBtn.setImageResource(R.drawable.thumb_up_green)
        } else {
            holder.dislikeBtn.setImageResource(R.drawable.thumb_up)
        }


        holder.dislikeBtn.setOnClickListener {
            if (userDislikeList.contains(tip.id)) {
                holder.dislikeBtn.setImageResource(R.drawable.thumb_up)
            } else {
                holder.dislikeBtn.setImageResource(R.drawable.thumb_up_green)
            }

            onTipDislike.invoke(tip, position)
        }

        holder.likeBtn.setOnClickListener {
            if (!userLikeList.contains(tip.id)) {
                holder.likeBtn.setImageResource(R.drawable.heart_fill)
            } else {
                holder.likeBtn.setImageResource(R.drawable.favorite_fill0_wght400_grad0_opsz24)
            }
            onTipLike.invoke(tip, position)
        }

        when (tipData) {
            is TipAdapterData.AllTips -> {
                ViewCompat.setBackgroundTintList(
                    holder.card,
                    ColorStateList.valueOf(colorNormal!!)
                )
            }

            is TipAdapterData.NewTips -> {
                ViewCompat.setBackgroundTintList(
                    holder.card,
                    ColorStateList.valueOf(colorHighlight!!)
                )
            }
        }
    }
}