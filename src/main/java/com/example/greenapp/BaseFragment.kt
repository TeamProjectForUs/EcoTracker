package com.example.greenapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment


open class BaseFragment: Fragment() {

    fun getSharedViewModel() :SharedViewModel {
        val act = activity as MainActivity
        return act.getSharedViewModel()
    }
}

open class BaseMenuFragment  : BaseFragment() {

   open fun setupMenu() {

       activity?.let {
           if (it is MainActivity) {
               it.showMenu()
               it.setupNormalMenu()
           }
       }
   }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupMenu()
    }

}

open class BaseMenuProfileFragment  : BaseMenuFragment() {
    override fun setupMenu() {
        super.setupMenu()
        activity?.let {
            if (it is MainActivity) {
                it.setupProfileMenu()
            }
        }
    }
}