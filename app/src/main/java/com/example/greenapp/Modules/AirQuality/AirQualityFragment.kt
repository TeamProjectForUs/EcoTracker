package com.example.greenapp.modules.AirQuality

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.greenapp.BaseMenuFragment
import com.example.greenapp.MainActivity
import com.example.greenapp.R
import com.example.greenapp.databinding.FragmentAirQualityBinding

class AirQualityFragment : BaseMenuFragment() {


    private var _binding: FragmentAirQualityBinding? = null
    private val binding: FragmentAirQualityBinding get() = _binding!!
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAirQualityBinding.inflate(inflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedVm = getSharedViewModel()
        if ((activity as MainActivity).requestLocationPermissions()
            && sharedVm.airQuality.value == null
        ) {
            sharedVm.getAirQuality(requireContext())
        }
        binding.qualityTv.visibility = View.GONE
        binding.qualityBar.visibility = View.GONE


        sharedVm.airQuality.observe(viewLifecycleOwner) { airQuality ->
            if (airQuality == null) {
                // user did not give location permissions
                val permissionsRequestMessage = getString(R.string.please_give_permissions)

                binding.airQualityTv.text = permissionsRequestMessage
                Toast.makeText(requireContext(), permissionsRequestMessage, Toast.LENGTH_SHORT)
                    .show()
            } else if (airQuality.indexes.isEmpty()) {
                val noAirQualityServicesMessage = getString(R.string.no_air_quality_services)
                binding.airQualityTv.text = noAirQualityServicesMessage

            } else {
                val firstIndex = airQuality.indexes.first()
                binding.airQualityTv.text =
                    "The air quality in your area is: ${firstIndex.category}"
                binding.qualityTv.visibility = View.VISIBLE
                binding.qualityBar.visibility = View.VISIBLE
                val color =
                    Color.rgb(
                        firstIndex.color.red.toFloat(),
                        firstIndex.color.green.toFloat(),
                        firstIndex.color.blue.toFloat()
                    )
                binding.qualityBar.backgroundTintList = ColorStateList.valueOf(color)
            }
        }
    }

}