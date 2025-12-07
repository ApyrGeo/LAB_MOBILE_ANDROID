package com.example.lab_mobile.utils.helpers

import android.content.Context
import android.util.Log
import com.example.lab_mobile.core.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class NetworkSyncManager(private val context: Context) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var wasOffline = false

    fun startMonitoring() {
        Log.d(TAG, "NetworkSyncManager: Starting network monitoring")

        scope.launch {
            ConnectivityManagerNetworkMonitor(context).isOnline
                .distinctUntilChanged() // Only emit when connectivity changes
                .collect { isOnline ->
                    Log.d(TAG, "NetworkSyncManager: Network status changed - isOnline: $isOnline")

                    if (isOnline && wasOffline) {
                        // Device just came back online
                        Log.d(TAG, "NetworkSyncManager: Device came back online, triggering sync")
                        SyncWorkManager.triggerSync(context)
                    }

                    wasOffline = !isOnline
                }
        }
    }
}