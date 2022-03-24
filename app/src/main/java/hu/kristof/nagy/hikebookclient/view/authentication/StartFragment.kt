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
// Also based on the following:
// https://github.com/google-developer-training/android-kotlin-fundamentals-apps/tree/master/GuessTheWordViewModel
// https://developer.android.com/topic/libraries/architecture/viewmodel
// https://github.com/google-developer-training/android-kotlin-fundamentals-apps/tree/master/GuessTheWordLiveData

package hu.kristof.nagy.hikebookclient.view.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.databinding.FragmentStartBinding
import hu.kristof.nagy.hikebookclient.model.UserAuth
import hu.kristof.nagy.hikebookclient.viewModel.authentication.LoginViewModel

/**
 * A Fragment to log in the user.
 */
@AndroidEntryPoint
class StartFragment : Fragment() {
    private lateinit var binding: FragmentStartBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_start, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.startRegistrationButton.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_startFragment_to_registrationFragment)
        }
        binding.aboutPageButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_startFragment_to_aboutFragment
            )
        }

        binding.lifecycleOwner = viewLifecycleOwner

        val loginViewModel: LoginViewModel by viewModels()
        // TODO: handle multiple clicks
        binding.loginButton.setOnClickListener {
            onLogin(it, binding, loginViewModel)
        }
        loginViewModel.loginRes.observe(viewLifecycleOwner) { loginRes ->
            onLoginRes(loginRes)
        }
    }

    private fun onLogin(
        view: View,
        binding: FragmentStartBinding,
        loginViewModel: LoginViewModel
    ) {
        val name = binding.nameEditText.text.toString()
        val pswd = binding.passwordEditText.text.toString()
        try {
            loginViewModel.onLogin(UserAuth(name, pswd))
        } catch (e: IllegalArgumentException) {
            Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun onLoginRes(loginRes: Boolean) {
        if (loginRes) {
            this.findNavController().navigate(
                R.id.action_startFragment_to_mainActivity
            )
        } else {
            Toast.makeText(activity, getString(R.string.login_fail_msg), Toast.LENGTH_LONG).show()
        }
    }
}