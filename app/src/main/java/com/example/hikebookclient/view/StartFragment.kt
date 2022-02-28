package com.example.hikebookclient.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import com.example.hikebookclient.R

class StartFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val startFragment = inflater.inflate(R.layout.fragment_start, container, false)
        val startRegistrationButton = startFragment.findViewById<Button>(R.id.startRegistrationButton)
        startRegistrationButton.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_startFragment_to_registrationFragment)
        }
        return startFragment
    }
}