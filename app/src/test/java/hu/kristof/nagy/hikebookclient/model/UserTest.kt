/*
 * Copyright (C) 2019 The Android Open Source Project
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

// based on:
// https://github.com/googlecodelabs/android-testing

package hu.kristof.nagy.hikebookclient.model

import org.junit.Assert.assertThrows
import org.junit.Test

class UserTest {
    @Test
    fun `empty name or password`() {
        assertThrows(
            IllegalArgumentException::class.java
        ) { User("", "") }

        assertThrows(
            IllegalArgumentException::class.java
        ) { User("", "password") }

        assertThrows(
            IllegalArgumentException::class.java
        ) { User("name", "") }

        assertThrows(
            IllegalArgumentException::class.java
        ) { User("", "pass") }
    }

    @Test
    fun `illegal name`() {
        assertThrows(
            IllegalArgumentException::class.java
        ) {  User("/", "password") }

        assertThrows(
            IllegalArgumentException::class.java
        ) {  User("  /", "password") }

        assertThrows(
            IllegalArgumentException::class.java
        ) { User("user/1", "password") }

        assertThrows(
            IllegalArgumentException::class.java
        ) { User("///", "password") }
    }

    @Test
    fun testPasswordLength() {
        assertThrows(
            IllegalArgumentException::class.java
        ) { User("user", "p") }

        assertThrows(
            IllegalArgumentException::class.java
        ) { User("user", "pa") }

        assertThrows(
            IllegalArgumentException::class.java
        ) { User("user", "pas") }

        assertThrows(
            IllegalArgumentException::class.java
        ) { User("user", "pass") }

        assertThrows(
            IllegalArgumentException::class.java
        ) { User("user", "passw") }
    }
}