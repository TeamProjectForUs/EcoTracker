package com.example.greenapp.modules.Tips

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.greenapp.models.Goal
import com.example.greenapp.database.Model
import com.example.greenapp.models.MyTip
import com.example.greenapp.models.Tip

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
    var allTipsLiveData: LiveData<List<Tip>> = Model.instance.tipsRepository.getAllTips(viewModelScope,allTipsLoadingState)
    var newTipsLiveData = allTipsLiveData.map {
        it.filter { tip -> tip.isNew() }
    }

    override fun likeTip(
        userLikeList: MutableList<String>,
        tip: Tip,
    ) {
        Model.instance.tipsRepository.toggleTipLike(viewModelScope, MyTip.fromTip(tip), userLikeList)
    }

    override fun dislikeTip(
        userDislikeList: MutableList<String>,
        tip: Tip,
    ) {
        Model.instance.tipsRepository.dislikeTip(tip, userDislikeList)
    }

    override fun undoDislikeTip(
        userDislikeList: MutableList<String>,
        tip: Tip,
    ) {
        Model.instance.tipsRepository.undoDislikeTip(tip, userDislikeList)
    }

    fun refreshTips() {
        Model.instance.tipsRepository.refreshAllTips(viewModelScope,allTipsLoadingState)
    }

}