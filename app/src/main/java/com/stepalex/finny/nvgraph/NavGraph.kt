package com.stepalex.finny.nvgraph

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.stepalex.finny.presentation.home.HomeScreen
import com.stepalex.finny.presentation.home.HomeViewModel
import com.stepalex.finny.presentation.quiz.QuizScreen
import com.stepalex.finny.presentation.splash.SplashScreen
import com.stepalex.finny.presentation.splash.SplashViewModel

@Composable
fun NavGraph(startDestination: String) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination) {
        navigation(
            route = Route.SplashNavigation.route,
            startDestination = Route.SplashScreen.route
        ) {
            composable(route = Route.SplashScreen.route) {
                val viewModel: SplashViewModel = hiltViewModel()
                SplashScreen(onSyncComplete = {
                    navController.navigate(Route.HomeNavigation.route) {
                        // Удаляем SplashNavigation и всё, что внутри него, из истории
                        popUpTo(Route.SplashNavigation.route) { inclusive = true }
                    }
                }, viewModel = viewModel)
            }
        }

        navigation(route = Route.HomeNavigation.route, startDestination = Route.HomeScreen.route) {
            composable(route = Route.HomeScreen.route) {
                val viewModel: HomeViewModel = hiltViewModel()
                LaunchedEffect(key1 = true) {
                    viewModel.uiEvent.collect { event ->
                        when (event) {
                            HomeUIEvent.OpenQuiz -> navController.navigate(Route.QuizScreen.route)
                        }
                    }
                }
                // Подключаем состояние правильно (как чинили раньше)
                //val state by viewModel.homeState.collectAsStateWithLifecycle()
                HomeScreen(event = viewModel::onEvent, state = viewModel.homeState)
            }

            dialog(route = "quizScreen") {
                QuizScreen(onDismiss = { navController.popBackStack() })
            }
        }


    }
}