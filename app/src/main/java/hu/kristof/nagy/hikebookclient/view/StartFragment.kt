/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// parts of this code have been copied from:
// https://github.com/googlecodelabs/android-navigation
// I have not made any significant changes (only renaming).
// Also based on the following:
// https://github.com/google-developer-training/android-kotlin-fundamentals-apps/tree/master/GuessTheWordViewModel
// https://developer.android.com/topic/libraries/architecture/viewmodel

package hu.kristof.nagy.hikebookclient.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.hikebookclient.R
import com.example.hikebookclient.databinding.FragmentStartBinding
import com.google.android.material.snackbar.Snackbar
import hu.kristof.nagy.hikebookclient.viewModel.LoginViewModel
import kotlin.math.log10

class StartFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentStartBinding>(inflater,
            R.layout.fragment_start, container, false)
        binding.startRegistrationButton.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_startFragment_to_registrationFragment)
        }

        val loginViewModel: LoginViewModel by viewModels()
        binding.viewModel = loginViewModel

        // testing viewModel, can remove
        binding.loginButton.setOnClickListener {
            loginViewModel.name = binding.nameEditText.text.toString()
            loginViewModel.pswd = binding.passwordEditText.text.toString()
            Toast.makeText(activity, loginViewModel.onLogin(), Toast.LENGTH_LONG).show()
        }

        return binding.root
    }
}