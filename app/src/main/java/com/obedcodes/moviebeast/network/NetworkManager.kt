package com.obedcodes.moviebeast.network

import okhttp3.OkHttpClient
import okhttp3.Request
import com.google.gson.Gson
import com.obedcodes.moviebeast.data.Todo
import java.io.IOException

class NetworkManager {
    private val client = OkHttpClient()
    private val gson = Gson()

    fun fetchTodo(apiUrl: String): Todo? {
        try {
            // Validate URL format
            if (!apiUrl.startsWith("http://") && !apiUrl.startsWith("https://")) {
                throw IllegalArgumentException("Invalid URL: $apiUrl")
            }

            val request = Request.Builder()
                .url(apiUrl)
                .build()

            // Execute network call
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    response.body()?.string()?.let { responseBody ->
                        return gson.fromJson(responseBody, Todo::class.java)
                    }
                } else {
                    // Handle HTTP errors
                    println("HTTP error: ${response.code()} - ${response.message()}")
                }
            }
        } catch (e: IOException) {
            // Handle network-related exceptions (e.g., no internet)
            println("Network error: ${e.message}")
        } catch (e: IllegalArgumentException) {
            // Handle invalid URL
            println("Invalid URL error: ${e.message}")
        } catch (e: Exception) {
            // Handle other unexpected exceptions
            println("Unexpected error: ${e.message}")
        }

        // Return null if something went wrong
        return null
    }
}
