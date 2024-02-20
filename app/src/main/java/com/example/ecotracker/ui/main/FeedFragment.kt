package com.example.ecotracker.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecotracker.Post
import com.example.ecotracker.PostAdapter
import com.example.ecotracker.R

class FeedFragment : Fragment() {


    private lateinit var feedButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_feed, container, false)


        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        feedButton = view.findViewById(R.id.feedbutton)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Initialize RecyclerView adapter with empty list
        postAdapter = PostAdapter(emptyList())
        recyclerView.adapter = postAdapter

        // Set a click listener on the feedButton to reload the data in the RecyclerView
        feedButton.setOnClickListener {
            // Fetch updated data (for example, from a database or network)
            val updatedData = fetchData()

            // Update the RecyclerView with the new data
            updateRecyclerView(updatedData)
        }
    }
    private fun fetchData(): List<Post> {

        // Replace this with your actual data fetching logic (e.g., database query or network request)
        // For demonstration purposes, returning dummy data here
        return listOf(
            Post("Updated Post 1", "Updated Content 1"),
            Post("Updated Post 2", "Updated Content 2")
            // Add more updated posts as needed
        )
    }

    private fun updateRecyclerView(updatedData: List<Post>) {
        // Update the adapter's data set with the new data
        // postAdapter.
        postAdapter.setData(updatedData)
    }
}
