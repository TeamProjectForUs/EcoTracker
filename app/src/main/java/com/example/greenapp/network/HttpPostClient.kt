package com.example.greenapp.network

import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class HttpPostClient {
    suspend inline fun <reified T> post(url: String, body: String): T? =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json; utf-8")
                connection.setRequestProperty("Accept", "application/json")
                connection.doOutput = true

                connection.outputStream.use { os ->
                    val input = body.toByteArray(charset("utf-8"))
                    os.write(input, 0, input.size)
                }
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val g = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").create()
                g.fromJson(response, T::class.java)
            } catch (e: Exception) {
                null
            }
        }
}