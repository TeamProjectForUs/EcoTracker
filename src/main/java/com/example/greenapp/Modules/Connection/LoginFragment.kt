package com.example.greenapp.Modules.Connection

import android.R.attr
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.greenapp.Model.Model
import com.example.greenapp.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth



class LoginFragment : Fragment() {

    private var nameTextField: EditText? = null
    private var PasswordTextField: EditText? = null
    private var messageTextView: TextView? = null
    private var ConnectButton: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

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

            Model.instance.login(email,password,requireActivity()){
                if(it){
                    Toast.makeText(context, " login successful.", Toast.LENGTH_SHORT).show()
                    Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_feedFragment)
                }
                else{
                    Toast.makeText(context, " login failed.", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
      menu.clear()
      super.onCreateOptionsMenu(menu, inflater)
  }

}