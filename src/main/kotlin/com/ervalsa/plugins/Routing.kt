package com.ervalsa.plugins

import com.ervalsa.routes.getAllHeroes
import com.ervalsa.routes.root
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.configureRouting() {
    routing {
        root()
        getAllHeroes()
    }
}
