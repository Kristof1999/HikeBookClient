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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.data.network.handleResult
import hu.kristof.nagy.hikebookclient.databinding.FragmentStartBinding
import hu.kristof.nagy.hikebookclient.viewModel.authentication.LoginViewModel

/**
 * A Fragment to log in the user.
 */
@AndroidEntryPoint
class StartFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val loginViewModel: LoginViewModel by viewModels()

        val binding = FragmentStartBinding.inflate(inflater, container, false)
            .apply {
                lifecycleOwner = viewLifecycleOwner
                viewModel = loginViewModel
                context = requireContext()
                executePendingBindings()
            }

        setupObserver(loginViewModel)

        setClickListeners(binding)

        return binding.root
    }

    private fun setupObserver(loginViewModel: LoginViewModel) {
        loginViewModel.loginRes.observe(viewLifecycleOwner) { res ->
            handleResult(context, res) { loginRes ->
                onLoginRes(loginRes)
            }
        }
    }

    private fun setClickListeners(binding: FragmentStartBinding) = with(binding) {
        startRegistrationButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_startFragment_to_registrationFragment
            )
        }
        aboutPageButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_startFragment_to_aboutFragment
            )
        }
    }

    private fun onLoginRes(loginRes: Boolean) {
        if (loginRes) {
            findNavController().navigate(
                R.id.action_startFragment_to_mainActivity
            )
        } else {
            Toast.makeText(activity, getString(R.string.login_fail_msg), Toast.LENGTH_LONG).show()
        }
    }
}