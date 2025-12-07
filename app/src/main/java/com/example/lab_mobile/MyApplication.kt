/*
 * Copyright (C) 2022 The Android Open Source Project
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
package com.example.lab_mobile

import android.app.Application
import android.util.Log
import com.example.lab_mobile.core.TAG
import com.example.lab_mobile.utils.helpers.NetworkSyncManager
import com.example.lab_mobile.utils.helpers.SyncWorkManager
import com.example.lab_mobile.utils.ui.createSyncNotificationChannel

class MyApplication : Application() {
    lateinit var container: AppContainer
    private lateinit var networkSyncManager: NetworkSyncManager

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "init")
        container = AppContainer(this)

        createSyncNotificationChannel(this)

        SyncWorkManager.initialize(this)

        networkSyncManager = NetworkSyncManager(this)
        networkSyncManager.startMonitoring()
    }
}
