package com.stepalex.finny.nvgraph

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.stepalex.finny.presentation.home.HomeScreen
import com.stepalex.finny.presentation.home.HomeViewModel

@Composable
fun NavGraph(startDestination: String) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination) {
        navigation(route = Route.HomeNavigation.route, startDestination = Route.HomeScreen.route) {
            composable(route = Route.HomeScreen.route) {
                val viewModel: HomeViewModel = hiltViewModel()
                LaunchedEffect(key1 = true) {
                    viewModel.uiEvent.collect { event ->
                        when (event) {

                            else -> {

                            }
                        }
                    }
                }
                HomeScreen(event = viewModel::onEvent, state = viewModel.homeState)
            }
        }
    }
}