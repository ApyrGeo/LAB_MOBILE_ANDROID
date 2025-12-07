package com.example.lab_mobile.utils.helpers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.lab_mobile.todo.data.local.OperationType
import com.example.lab_mobile.MyAppDatabase
import com.example.lab_mobile.core.TAG
import com.example.lab_mobile.core.data.remote.Api
import com.example.lab_mobile.todo.data.remote.ItemService
import com.example.lab_mobile.utils.ui.dismissSyncNotification
import com.example.lab_mobile.utils.ui.showSyncCompletedNotification
import com.example.lab_mobile.utils.ui.showSyncInProgressNotification

class SyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val database = MyAppDatabase.getDatabase(context)
    private val itemDao = database.itemDao()
    private val pendingOperationDao = database.pendingOperationDao()
    private val itemService = Api.retrofit.create(ItemService::class.java)

    override suspend fun doWork(): Result {
        Log.d(TAG, "SyncWorker: Starting sync...")

        return try {
            val token = Api.tokenInterceptor.token
            if (token.isNullOrBlank()) {
                Log.w(TAG, "SyncWorker: No auth token available, skipping sync")
                return Result.success()
            }

            val bearerToken = "Bearer $token"

            // Get all pending operations
            val pendingOperations = pendingOperationDao.getAllPendingOperations()
            Log.d(TAG, "SyncWorker: Found ${pendingOperations.size} pending operations")

            if (pendingOperations.isEmpty()) {
                return Result.success()
            }
            showSyncInProgressNotification(applicationContext, pendingOperations.size)

            var successCount = 0
            var failureCount = 0
            // Process each pending operation
            for (operation in pendingOperations) {
                try {
                    when (operation.operationType) {
                        OperationType.CREATE -> {
                            val item = itemDao.getById(operation.itemId)
                            if (item != null) {
                                Log.d(TAG, "SyncWorker: Creating item ${item._id} on server")
                                val createdItem = itemService.create(bearerToken, item)
                                // If the local item had a temp ID, delete it and insert the server version
                                if (item._id.startsWith("temp_")) {
                                    Log.d(TAG, "SyncWorker: Replacing temp item ${item._id} with server item ${createdItem._id}")
                                    itemDao.deleteById(item._id)
                                    itemDao.insert(createdItem.copy(needsSync = false))
                                } else {
                                    // Update the existing item
                                    itemDao.update(createdItem.copy(needsSync = false))
                                }

                                pendingOperationDao.deleteById(operation.id)
                                successCount++
                                Log.d(TAG, "SyncWorker: Successfully created item on server with ID ${createdItem._id}")
                            } else {
                                // Item was deleted locally, remove pending operation
                                pendingOperationDao.deleteById(operation.id)
                            }
                        }
                        OperationType.UPDATE -> {
                            val item = itemDao.getById(operation.itemId)
                            if (item != null) {
                                Log.d(TAG, "SyncWorker: Updating item ${item._id} on server")

                                val updatedItem = itemService.update(bearerToken, item._id, item)

                                itemDao.update(updatedItem.copy(needsSync = false))
                                pendingOperationDao.deleteById(operation.id)
                                successCount++
                                Log.d(TAG, "SyncWorker: Successfully updated item ${item._id}")
                            } else {
                                pendingOperationDao.deleteById(operation.id)
                            }
                        }
                        OperationType.DELETE -> {
                            Log.d(TAG, "SyncWorker: DELETE operation for item ${operation.itemId} - handling not implemented yet")
                            pendingOperationDao.deleteById(operation.id)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(
                        TAG,
                        "SyncWorker: Failed to sync operation ${operation.id} for item ${operation.itemId}",
                        e
                    )
                    failureCount++
                }
            }

            Log.d(TAG, "SyncWorker: Sync completed - Success: $successCount, Failures: $failureCount")
            dismissSyncNotification(applicationContext)
            // Show completion notification if any items were synced
            if (successCount > 0) {
                showSyncCompletedNotification(
                    applicationContext,
                    syncedCount = successCount,
                    failedCount = failureCount
                )
            }

            if (failureCount > 0) {
                // Some operations failed, retry later
                Result.retry()
            } else {
                Log.d(TAG, "SyncWorker: All pending operations synced. UI will refresh when appropriate.")
                Result.success()
            }
        } catch (e: Exception) {
            Log.e(TAG, "SyncWorker: Sync failed with exception", e)
            dismissSyncNotification(applicationContext)
            Result.retry()
        }
    }
}