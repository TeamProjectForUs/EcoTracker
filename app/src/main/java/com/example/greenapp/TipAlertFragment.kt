package com.example.greenapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class TipAlertFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view= inflater.inflate(R.layout.fragment_tip_alert, container, false)
        var messageTextView:TextView?= view?.findViewById(R.id.alertMessage)
//        Model.instance.getAllTips {
//            var tip: Tip? = it?.get(0)
//            messageTextView?.setText(tip?.description)
//        }
        return view

    }



}