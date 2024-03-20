package com.example.greenapp.modules.Auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.greenapp.database.Model
import com.example.greenapp.R


class LoginFragment : Fragment() {

    private var nameTextField: EditText? = null
    private var PasswordTextField: EditText? = null
    private var messageTextView: TextView? = null
    private var ConnectButton: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        setupUI(view)
        return view
    }
    private fun setupUI(view: View) {


        nameTextField = view.findViewById(R.id.username)
        PasswordTextField = view.findViewById(R.id.Password)
        ConnectButton = view.findViewById(R.id.Connectbtn)
        messageTextView?.text = ""

        ConnectButton?.setOnClickListener {

            val email=nameTextField?.text.toString()
            val password=PasswordTextField?.text.toString()
            ConnectButton?.isEnabled = false
            Model.instance.userRepository.login(lifecycleScope, email,password,requireActivity()){
                if(it){
                    Toast.makeText(context, " login successful.", Toast.LENGTH_SHORT).show()
                    Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_feedFragment)

                }
                else{
                    Toast.makeText(context, " login failed.", Toast.LENGTH_SHORT).show()
                }
                ConnectButton?.isEnabled = true
            }
        }
    }


}