package com.example.greenapp.modules.Tips

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greenapp.BaseMenuFragment
import com.example.greenapp.database.Model
import com.example.greenapp.models.Tip
import com.example.greenapp.R
import com.example.greenapp.adapters.TipsAdapter
import com.example.greenapp.databinding.FragmentTipsBinding
import com.example.greenapp.modules.Profile.createNotificationsMenu

class TipsFragment : BaseMenuFragment() {


    var _binding: FragmentTipsBinding? = null
    val binding: FragmentTipsBinding get() = _binding!!

    private lateinit var tipsViewModel: TipsViewModel


    private lateinit var tipsAdapter: TipsAdapter
    private lateinit var tipsRecyclerView: RecyclerView

    private var colorHighlight: Int = 0
    private var colorNormal: Int = 0

    private var tipsTypeAll = TipsAdapter.TipAdapterData.AllTips(listOf())
    private var tipsTypeNew = TipsAdapter.TipAdapterData.NewTips(listOf())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTipsBinding.inflate(inflater)
        tipsViewModel = ViewModelProvider(this)[TipsViewModel::class.java]
        return binding.root
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tipsRecyclerView = binding.rvTips

        val allTipsBtn = binding.allTipsBtn
        val newTipsBtn = binding.newTipsBtn

        val addPostBtn = binding.addPostBtn

        addPostBtn.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_global_addPostFragment)
        }


        binding.pullToRefresh.setOnRefreshListener {
            binding.progressBar.visibility = VISIBLE
            tipsViewModel.refreshTips()
            binding.progressBar.visibility = GONE
        }

        binding.progressBar.visibility = VISIBLE

        colorHighlight = ContextCompat.getColor(requireContext(), R.color.strockGreenLight)
        colorNormal = ContextCompat.getColor(requireContext(), R.color.white)


        allTipsBtn.setOnClickListener {
            ViewCompat.setBackgroundTintList(allTipsBtn, ColorStateList.valueOf(colorHighlight))
            ViewCompat.setBackgroundTintList(newTipsBtn, ColorStateList.valueOf(colorNormal))
            tipsAdapter.setTipsData(tipsTypeAll)
        }


        newTipsBtn.setOnClickListener {
            ViewCompat.setBackgroundTintList(allTipsBtn, ColorStateList.valueOf(colorNormal))
            ViewCompat.setBackgroundTintList(newTipsBtn, ColorStateList.valueOf(colorHighlight))
            tipsAdapter.setTipsData(tipsTypeNew)
        }

        tipsViewModel.allTipsLiveData.observe(viewLifecycleOwner) {
            tipsTypeAll = TipsAdapter.TipAdapterData.AllTips(it)
            binding.progressBar.visibility = GONE
            if (tipsAdapter.isAllTips()) {
                tipsAdapter.setTipsData(tipsTypeAll)
            }
        }

        createNotificationsMenu(binding.notificationsBtn)

        tipsAdapter = createTipsAdapter(tipsTypeAll, tipsViewModel)

        tipsViewModel.newTipsLiveData.observe(viewLifecycleOwner) {
            tipsTypeNew = TipsAdapter.TipAdapterData.NewTips(it)
            if (!tipsAdapter.isAllTips()) {
                tipsAdapter.setTipsData(tipsTypeNew)
            }
        }
        tipsViewModel.allTipsLoadingState.observe(viewLifecycleOwner) { state ->
            binding.pullToRefresh.isRefreshing = state == Model.LoadingState.LOADING
        }


        tipsRecyclerView.adapter = tipsAdapter
        tipsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

    }
}


fun BaseMenuFragment.createTipsAdapter(
    tipsType: TipsAdapter.TipAdapterData,
    tipsRepository: TipsRepository,
    isLikedPage: Boolean = false,
    onMakeGoal: (Tip, Int) -> Unit = { _, _ -> },
): TipsAdapter {
    val sharedVm = getSharedViewModel()
    var tipsAdapter: TipsAdapter? = null
    tipsAdapter = TipsAdapter(
        tipData = tipsType,
        onTipDislike = { tip, position ->
            sharedVm.currentUser.value?.let { user ->
                if (user.tipDislikeList.contains(tip.id)) {
                    tipsRepository.undoDislikeTip(
                        user.tipDislikeList,
                        tip
                    )
                } else {
                    tipsRepository.dislikeTip(
                        user.tipDislikeList,
                        tip
                    )
                }
                tipsAdapter?.updateUserDislikeList(user.tipDislikeList, position)
            }
        },
        onTipLike = { tip, position ->
            sharedVm.currentUser.value?.let { user ->
                tipsRepository.likeTip(user.currentLikeList, tip)
                tipsAdapter?.updateUserLikeList(user.currentLikeList, position)
            }
        },
        onMakeGoal = onMakeGoal,
        isLikedPage = isLikedPage,
    )
    sharedVm.currentUser.observe(viewLifecycleOwner) {
        it?.let { user ->
            tipsAdapter.updateUserData(user)
        }
    }
    return tipsAdapter
}