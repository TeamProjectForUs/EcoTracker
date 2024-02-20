package com.example.ecotracker.ui.auth;

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.ecotracker.MainActivity
import com.example.ecotracker.R


class LoginFragment : Fragment() {

    private var nameTextField: EditText? = null
    private var PasswordTextField: EditText? = null
    private var messageTextView: TextView? = null
    private var ConnectButton: Button? = null
    private var cancelButton: Button? = null

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
        // messageTextView = view.findViewById(R.id.Connectbtn)
        ConnectButton = view.findViewById(R.id.Connectbtn)
        // cancelButton = view.findViewById(R.id.btnAddStudentCancel)
        messageTextView?.text = ""

        ConnectButton?.setOnClickListener {
            activity?.finish()
            startActivity(Intent(context, MainActivity::class.java))
        }
//        cancelButton?.setOnClickListener {
//            Navigation.findNavController(it).popBackStack(R.id.studentsFragment, false)
//        }
    }



  // on click?
//            val name = nameTextField?.text.toString()
//            val password = PasswordTextField?.text.toString()
//
//            val student = Student(name, id, "", false)
//            Model.instance.addStudent(student) {
//                Navigation.findNavController(it).popBackStack(R.id.studentsFragment, false)
//            }
//        }
}