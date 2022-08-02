package com.ervalsa.plugins

import com.ervalsa.routes.getAllHeroes
import com.ervalsa.routes.root
import com.ervalsa.routes.searchHeroes
import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.configureRouting() {
    routing {
        root()
        getAllHeroes()
        searchHeroes()

        static("/images") {
            resources("images")
        }
    }
}
