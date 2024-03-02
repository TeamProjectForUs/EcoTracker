package com.example.greenapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.navigation.fragment.navArgs
import com.example.greenapp.Model.Model
import com.example.greenapp.Model.User
import com.example.greenapp.Modules.Posts.PostFullViewFragmentArgs
import com.google.android.material.button.MaterialButton
import com.squareup.picasso.Picasso

class ProfileFragment : BaseMenuProfileFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_profile, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getSharedViewModel().currentUser.observe(viewLifecycleOwner) { user ->

            view.findViewById<TextView>(R.id.userNameTv).text = user.name
        }
    }


}