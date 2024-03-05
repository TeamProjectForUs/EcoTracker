package com.example.greenapp.Modules.Tips

import android.adservices.topics.Topic
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.greenapp.Model.Goal
import com.example.greenapp.Model.Model
import com.example.greenapp.Model.Tip

interface TipsRepository {
    fun dislikeTip(
        userDislikeList: MutableList<String>,
        tip: Tip,
    )
    fun undoDislikeTip(
        userDislikeList: MutableList<String>,
        tip: Tip,
    )
    fun likeTip(
        userLikeList: MutableList<String>,
        tip: Tip,
    )

}


interface TipsGoalsRepository : TipsRepository {
    fun toggleGoalTip(
        userGoalsList: MutableList<Goal>,
        tip: Tip)
}
class TipsViewModel : ViewModel(), TipsRepository {

    val allTipsLoadingState = MutableLiveData(Model.LoadingState.LOADED)
    var allTipsLiveData: MutableLiveData<List<Tip>> = Model.instance.getAllTips(allTipsLoadingState)
    var newTipsLiveData = allTipsLiveData.map {
        it.filter { tip -> tip.isNew() }
    }

    override fun likeTip(
        userLikeList: MutableList<String>,
        tip: Tip,
    ) {
        Model.instance.toggleTipLike(viewModelScope, tip, userLikeList)
    }


    override fun dislikeTip(
        userDislikeList: MutableList<String>,
        tip: Tip,
    ) {
        Model.instance.dislikeTip(tip, userDislikeList)
    }

    override fun undoDislikeTip(
        userDislikeList: MutableList<String>,
        tip: Tip,
    ) {
        Model.instance.undoDislikeTip(tip, userDislikeList)
    }

    fun refreshTips() {
        Model.instance.refreshAllTips(allTipsLoadingState, allTipsLiveData)
    }

}