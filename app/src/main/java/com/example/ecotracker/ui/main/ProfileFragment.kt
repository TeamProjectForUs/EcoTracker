package com.example.ecotracker.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import com.example.ecotracker.R

class ProfileFragment : Fragment() {

    private lateinit var allPostsButton: Button
    private lateinit var photoButton: Button
    private lateinit var favTipsButton: Button
    private lateinit var goalsButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        allPostsButton = view.findViewById(R.id.allPostsButton)
        photoButton = view.findViewById(R.id.photoButton)
        favTipsButton = view.findViewById(R.id.favTipsButton)
        goalsButton = view.findViewById(R.id.goalsButton)

        // Set click listeners for each button
        allPostsButton.setOnClickListener { onButtonClicked(allPostsButton) }
        photoButton.setOnClickListener { onButtonClicked(photoButton) }
        favTipsButton.setOnClickListener { onButtonClicked(favTipsButton) }
        goalsButton.setOnClickListener { onButtonClicked(goalsButton) }

        return view
    }

    private fun onButtonClicked(clickedButton: Button) {
        // Reset all buttons to default color
        resetButtonColors()

        // Set the clicked button color
        clickedButton.setBackgroundColor(ContextCompat.getColor(requireContext(),
            R.color.selected_button_color
        ))

        // You can handle additional logic here if needed
    }

    private fun resetButtonColors() {
        val buttons = listOf(allPostsButton, photoButton, favTipsButton, goalsButton)
        buttons.forEach { button ->
            button.setBackgroundColor(ContextCompat.getColor(requireContext(),
                R.color.default_button_color
            ))
        }
    }

}
