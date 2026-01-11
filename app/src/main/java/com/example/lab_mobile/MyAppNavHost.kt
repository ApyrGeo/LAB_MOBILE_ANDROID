package com.example.lab_mobile

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lab_mobile.auth.LoginScreen
import com.example.lab_mobile.core.data.UserPreferences
import com.example.lab_mobile.core.data.remote.Api
import com.example.lab_mobile.core.ui.UserPreferencesViewModel
import com.example.lab_mobile.todo.ui.item.ItemScreen
import com.example.lab_mobile.todo.ui.items.ItemsScreen

val itemsRoute = "items"
val authRoute = "auth"

@Composable
fun MyAppNavHost() {
    val navController = rememberNavController()
    val onCloseItem = {
        Log.d("MyAppNavHost", "navigate back to list")
        navController.popBackStack()
    }
    val userPreferencesViewModel =
        viewModel<UserPreferencesViewModel>(factory = UserPreferencesViewModel.Factory)
    val userPreferencesUiState by userPreferencesViewModel.uiState.collectAsStateWithLifecycle(
        initialValue = UserPreferences()
    )
    val myAppViewModel = viewModel<MyAppViewModel>(factory = MyAppViewModel.Factory)
    NavHost(
        navController = navController,
        startDestination = authRoute
    ) {
        composable(itemsRoute) {
            var screenVisible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) { screenVisible = true }
            AnimatedVisibility(
                visible = screenVisible,
                enter = fadeIn(animationSpec = tween(220)) + slideInHorizontally(animationSpec = tween(220)) { it / 8 },
                exit = fadeOut(animationSpec = tween(180)) + slideOutHorizontally(animationSpec = tween(180)) { it / 8 }
            ) {
                ItemsScreen(
                    onItemClick = { itemId ->
                        Log.d("MyAppNavHost", "navigate to item $itemId")
                        navController.navigate("$itemsRoute/$itemId")
                    },
                    onAddItem = {
                        Log.d("MyAppNavHost", "navigate to new item")
                        navController.navigate("$itemsRoute-new")
                    },
                    onLogout = {
                        Log.d("MyAppNavHost", "logout")
                        myAppViewModel.logout()
                        Api.tokenInterceptor.token = null
                        navController.navigate(authRoute) {
                            popUpTo(0)
                        }
                    })
            }
        }
        composable(
            route = "$itemsRoute/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) {
            var screenVisible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) { screenVisible = true }
            AnimatedVisibility(
                visible = screenVisible,
                enter = fadeIn(animationSpec = tween(220)) + slideInHorizontally(animationSpec = tween(220)) { it / 8 },
                exit = fadeOut(animationSpec = tween(180)) + slideOutHorizontally(animationSpec = tween(180)) { it / 8 }
            ) {
                ItemScreen(
                    itemId = it.arguments?.getString("id"),
                    onClose = { onCloseItem() }
                )
            }
        }
        composable(route = "$itemsRoute-new") {
            var screenVisible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) { screenVisible = true }
            AnimatedVisibility(
                visible = screenVisible,
                enter = fadeIn(animationSpec = tween(220)) + slideInHorizontally(animationSpec = tween(220)) { it / 8 },
                exit = fadeOut(animationSpec = tween(180)) + slideOutHorizontally(animationSpec = tween(180)) { it / 8 }
            ) {
                ItemScreen(
                    itemId = null,
                    onClose = { onCloseItem() }
                )
            }
        }
        composable(route = authRoute) {
            var screenVisible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) { screenVisible = true }
            AnimatedVisibility(
                visible = screenVisible,
                enter = fadeIn(animationSpec = tween(220)) + slideInHorizontally(animationSpec = tween(220)) { it / 8 },
                exit = fadeOut(animationSpec = tween(180)) + slideOutHorizontally(animationSpec = tween(180)) { it / 8 }
            ) {
                LoginScreen(
                    onClose = {
                        Log.d("MyAppNavHost", "navigate to list")
                        navController.navigate(itemsRoute)
                    }
                )
            }
        }
    }
    LaunchedEffect(userPreferencesUiState.token) {
        if (userPreferencesUiState.token.isNotEmpty()) {
            Log.d("MyAppNavHost", "Lauched effect navigate to items")
            Api.tokenInterceptor.token = userPreferencesUiState.token
            myAppViewModel.setToken(userPreferencesUiState.token)
            navController.navigate(itemsRoute) {
                popUpTo(0)
            }
        }
    }
}
