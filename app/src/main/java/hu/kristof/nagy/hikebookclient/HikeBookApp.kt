/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// All Hilt-related code is based on the following sources:
// https://developer.android.com/training/dependency-injection/hilt-android
// https://github.com/googlecodelabs/android-hilt
// I have not modified anything significant in this file (only renaming).

package hu.kristof.nagy.hikebookclient

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HikeBookApp : Application()