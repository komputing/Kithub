package org.ligi.kithub

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

open class GithubAPI(
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder().build(),
    internal val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
) {

    internal suspend fun executeGetCommand(command: String, token: String? = null): String? {
        val request = Request.Builder()
            .get()
            .header("Accept", "application/vnd.github.machine-man-preview+json")
            .url("https://api.github.com/$command")

        token?.run { request.header("Authorization", "Bearer $token") }

        val response = executeWithRetry(request.build())
        val res = response?.body?.use { it.string() }

        if (response == null || response.code / 100 != 2) {
            println("problem executing $command $res")
            return null
        }

        return res
    }

    internal suspend fun executeWithRetry(request: Request, attempts: Int = 5): Response? {
        repeat(attempts) {
            try {
                return okHttpClient.newCall(request).execute()
            } catch (ioe: IOException) {
                // we will retry in this case
                delay(1000)
            }

        }
        return null // we tried our best
    }
}