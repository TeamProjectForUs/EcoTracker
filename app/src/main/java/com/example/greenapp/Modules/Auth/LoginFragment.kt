package com.example.greenapp.modules.Auth

import android.app.AlertDialog
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
import com.google.android.material.button.MaterialButton
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


class LoginFragment : Fragment() {

    private var nameTextField: EditText? = null
    private var PasswordTextField: EditText? = null
    private var messageTextView: TextView? = null
    private var ConnectButton: Button? = null
    private var ForgotPwdbtn: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
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
        ForgotPwdbtn = view.findViewById(R.id.forgotPwd)
        messageTextView?.text = ""

        ForgotPwdbtn?.setOnClickListener {
            val changePasswordLayout =
                layoutInflater.inflate(R.layout.change_password_layout_with_input, null, false)

            val alert = AlertDialog.Builder(requireContext())
                .setTitle("EcoTracker")
                .setView(changePasswordLayout)
                .create()


            val emailEt = changePasswordLayout.findViewById<EditText>(R.id.etEmailAddress)
            val okBtn = changePasswordLayout.findViewById<MaterialButton>(R.id.changePasswordBtnYes)
            val cancelBtn =
                changePasswordLayout.findViewById<MaterialButton>(R.id.changePasswordBtnNo)
            cancelBtn.setOnClickListener {
                alert.dismiss()
            }

            okBtn.setOnClickListener {
                val email = emailEt.text.toString()
                if (email.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Please enter a valid email address",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // send the user a email to reset his password
                    Firebase.auth.sendPasswordResetEmail(email)
                        .addOnSuccessListener {
                            Toast.makeText(
                                requireContext(),
                                "We have sent you an email to reset your password. please check your inbox and your spam box",
                                Toast.LENGTH_SHORT
                            ).show()
                            alert.dismiss()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                            alert.dismiss()
                        }
                }
            }

            alert.show()
        }

        ConnectButton?.setOnClickListener {

            val email = nameTextField?.text.toString()
            val password = PasswordTextField?.text.toString()
            ConnectButton?.isEnabled = false
            Model.instance.userRepository.login(
                lifecycleScope,
                email,
                password,
                requireActivity()
            ) {
                if (it) {
                    Toast.makeText(context, " login successful.", Toast.LENGTH_SHORT).show()
                    Navigation.findNavController(view)
                        .navigate(R.id.action_loginFragment_to_feedFragment)

                } else {
                    Toast.makeText(context, " login failed.", Toast.LENGTH_SHORT).show()
                }
                ConnectButton?.isEnabled = true
            }
        }
    }


}