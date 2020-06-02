package com.example.config

import com.example.routers.author
import io.ktor.application.Application

/**
 * @author carl
 *
 */
@JvmOverloads
fun Application.routers(testing: Boolean = false) {
    author()
}