package com.example.greenapp

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.example.greenapp.Model.Model
import com.example.greenapp.Model.Tip


class MyTipsFragment : Fragment() {

    private var yesbtn: Button? = null
    private var messageTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_tips, container, false)
        setupUI(view)
        return view
    }

    fun setupUI(view: View) {
        Model.instance.getAllTips {
            var tip: Tip? = it?.get(0)

            if (tip != null) {
                TipAlert(tip)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }


    fun TipAlert(tip: Tip) {
        messageTextView = view?.findViewById(R.id.alertMessage)
        messageTextView?.setText(tip.description)
        val dilaogBinding = layoutInflater.inflate(R.layout.fragment_tip_alert, null)

        Toast.makeText(context, " ${tip.description}", Toast.LENGTH_SHORT).show()

        val myDialog = Dialog(requireActivity())
        myDialog.setContentView(dilaogBinding)

        myDialog.setCancelable(true)
        myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        myDialog.show()

        yesbtn = dilaogBinding.findViewById(R.id.alertYes)
        yesbtn?.setOnClickListener {
            myDialog.dismiss()
        }
    }

}