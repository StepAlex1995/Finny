package com.stepalex.finny.nvgraph

sealed class Route(val route: String) {
    data object HomeScreen : Route(route = "homeScreen")
    data object SplashScreen : Route(route = "splashScreen")
    //data object QuizScreen : Route(route = "quizScreen")

    data object HomeNavigation : Route(route = "homeNavigation")
    data object SplashNavigation : Route(route = "splashNavigation")
//    data object QuizNavigation : Route(route = "quizNavigation")
}