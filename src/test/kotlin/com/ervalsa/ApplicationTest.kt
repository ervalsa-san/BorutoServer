package com.ervalsa

import com.ervalsa.models.ApiResponse
import com.ervalsa.repository.HeroRepository
import com.ervalsa.repository.NEXT_PAGE_KEY
import com.ervalsa.repository.PREV_PAGE_KEY
import io.ktor.http.*
import kotlin.test.*
import io.ktor.server.testing.*
import io.ktor.application.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent.inject

class ApplicationTest {

    private val heroRepository: HeroRepository by inject(HeroRepository::class.java)

    @Test
    fun accessRootEndpoint_AssertCorrectInformation()  {
       withTestApplication(moduleFunction = Application::module) {
           handleRequest(HttpMethod.Get, "/").apply {
               assertEquals(HttpStatusCode.OK, response.status())
               assertEquals(
                   "Welcome to Boruto API",
                   response.content
               )
           }
       }
    }

    @Test
    fun accessAllHeroesEndpoint_QueryAllPages_AssertCorrectInformation() {
        withTestApplication(moduleFunction = Application::module) {
            val pages = 1..5
            val heroes = listOf(
                heroRepository.page1,
                heroRepository.page2,
                heroRepository.page3,
                heroRepository.page4,
                heroRepository.page5
            )

            pages.forEach { page ->
                handleRequest(HttpMethod.Get, "/boruto/heroes?page=$page").apply {
                    assertEquals(HttpStatusCode.OK, response.status())

                    val expected = ApiResponse(
                        success = true,
                        message = "Ok",
                        prevPage = calculatePage(page)["prevPage"],
                        nextPage = calculatePage(page)["nextPage"],
                        heroes = heroes[page - 1]
                    )

                    val actual = Json.decodeFromString<ApiResponse>(response.content.toString())

                    assertEquals(
                        expected,
                        actual
                    )
                }
            }
        }
    }

    private fun calculatePage(page: Int) : Map<String, Int?> {
        var prevPage: Int? = page
        var nextPage: Int? = page

        if (page in 1..4) {
            nextPage = nextPage?.plus(1)
        }

        if (page in 2..5) {
            prevPage = prevPage?.minus(1)
        }

        if (page == 1) {
            prevPage = null
        }

        if (page == 5) {
            nextPage = null
        }

        return mapOf(PREV_PAGE_KEY to prevPage, NEXT_PAGE_KEY to nextPage)
    }

    @Test
    fun accessAllHeroesEndpoint_QueryNonExistingPageNumber_AssertError() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes?page=6").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())

                val expected = ApiResponse(
                    success = false,
                    message = "Heroes Not Found."
                )

                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())

                assertEquals(
                    expected,
                    actual
                )
            }
        }
    }

    @Test
    fun accessAllHeroesEndpoint_QueryInvalidPageNumber_AssertError() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes?page=invalid").apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())

                val expected = ApiResponse(
                    success = false,
                    message = "Only Numbers Allowed."
                )

                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())

                assertEquals(
                    expected,
                    actual
                )
            }
        }
    }

    @Test
    fun accessSearchHeroesEndpoint_QueryHeroName_AssertSingleHeroResult() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes/search?name=sas").apply {
                assertEquals(HttpStatusCode.OK, response.status())

                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                    .heroes.size

                assertEquals(1, actual)
            }
        }
    }

    @Test
    fun accessSearchHeroesEndpoint_QueryHeroName_AssertMultipleHeroResult() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes/search?name=sa").apply {
                assertEquals(HttpStatusCode.OK, response.status())

                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                    .heroes.size

                assertEquals(3, actual)
            }
        }
    }

    @Test
    fun accessSearchHeroesEndpoint_QueryEmptyText_AssertEmptyListResult() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes/search?name=").apply {
                assertEquals(HttpStatusCode.OK, response.status())

                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                    .heroes

                assertEquals(emptyList(), actual)
            }
        }
    }

    @Test
    fun accessSearchHeroesEndpoint_QueryNonExistingHero_AssertEmptyListResult() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes/search?name=unknown").apply {
                assertEquals(HttpStatusCode.OK, response.status())

                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                    .heroes

                assertEquals(emptyList(), actual)
            }
        }
    }

    @Test
    fun accessNonExistingEndpoint_AssertNotFound() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/unknown").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
                assertEquals("Page not Found.", response.content)
            }
        }
    }
}