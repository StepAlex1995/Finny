package com.stepalex.finny.nvgraph

sealed class Route(val route: String) {
    object HomeScreen : Route(route = "homeScreen")

    object HomeNavigation : Route(route = "homeNavigation")
}