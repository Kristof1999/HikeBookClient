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
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import hu.kristof.nagy.hikebookclient.R
import hu.kristof.nagy.hikebookclient.databinding.FragmentRegistrationBinding
import hu.kristof.nagy.hikebookclient.model.User
import hu.kristof.nagy.hikebookclient.util.catchAndShowIllegalStateAndArgument
import hu.kristof.nagy.hikebookclient.util.handleOffline
import hu.kristof.nagy.hikebookclient.viewModel.authentication.RegistrationViewModel

/**
 * A Fragment to register the user.
 */
@AndroidEntryPoint
class RegistrationFragment : Fragment() {
    private lateinit var binding: FragmentRegistrationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_registration, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val registrationViewModel : RegistrationViewModel by viewModels()

        setupRegistration(registrationViewModel)
    }

    private fun setupRegistration(registrationViewModel: RegistrationViewModel) {
        binding.lifecycleOwner = viewLifecycleOwner
        registrationViewModel.registrationRes.observe(viewLifecycleOwner) { registrationRes ->
            onRegistrationRes(registrationRes)
        }
        binding.registerButton.setOnClickListener {
            onRegister(binding, registrationViewModel)
        }
    }

    private fun onRegister(
        binding: FragmentRegistrationBinding,
        registrationViewModel: RegistrationViewModel
    ) {
        val name = binding.registerNameEditText.text.toString()
        val pswd = binding.registerPasswordEditText.text.toString()
        catchAndShowIllegalStateAndArgument(requireContext()) {
            handleOffline(requireContext()) {
                registrationViewModel.onRegister(User(name, pswd))
            }
        }
    }

    private fun onRegistrationRes(registrationRes: Boolean) {
        if (registrationRes) {
            findNavController().navigate(
                R.id.action_registrationFragment_to_mainActivity
            )
        } else {
            Toast.makeText(activity, getString(R.string.name_exists_error_msg), Toast.LENGTH_LONG).show()
        }
    }
}