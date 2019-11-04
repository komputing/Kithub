package org.ligi.kithub

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.delay
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.ligi.kithub.model.*
import java.io.File
import java.io.IOException
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*

private val JSONMediaType: MediaType = "application/json".toMediaType()

open class GithubApplicationAPI(private val integration: String,
                                private val cert: File,
                                private val okHttpClient: OkHttpClient = OkHttpClient.Builder().build(),
                                moshi: Moshi = Moshi.Builder().build()) {

    val tokenResponseAdapter = moshi.adapter(TokenResponse::class.java)!!
    val commitStatusAdapter = moshi.adapter(GithubCommitStatus::class.java)!!
    val githubLabelAdapter = moshi.adapter(GithubLabel::class.java)!!
    val githubIssueAdapter = moshi.adapter(GithubIssue::class.java)!!
    val pushEventAdapter = moshi.adapter(GithubPushEvent::class.java)!!
    val deleteEventAdapter = moshi.adapter(GithubDeleteEvent::class.java)!!
    val pullRequestEventAdapter = moshi.adapter(GithubPullRequestEvent::class.java)!!

    var pgpKeyInfoListType = Types.newParameterizedType(List::class.java, GithubPGPKeyInfo::class.java)
    var githubPGPKeyInfoAdapter = moshi.adapter<List<GithubPGPKeyInfo>>(pgpKeyInfoListType)


    private fun obtain_private_key(private_key_file: File): PrivateKey {
        val privateKeyBytes = private_key_file.readBytes()
        val encodedKeySpec = PKCS8EncodedKeySpec(privateKeyBytes)
        return KeyFactory.getInstance("RSA").generatePrivate(encodedKeySpec)
    }

    suspend fun getToken(installation: String): String? {

        val claimsSet = JWTClaimsSet.Builder()
                .issuer(integration)
                .issueTime(Date())
                .expirationTime(Date(Date().time))
                .build()

        val signer = RSASSASigner(obtain_private_key(cert))

        val signedJWT = SignedJWT(
                JWSHeader(JWSAlgorithm.RS256),
                claimsSet)

        signedJWT.sign(signer)

        val jwt = signedJWT.serialize()

        val execute = executePostCommand(
                command = "installations/$installation/access_tokens",
                token = jwt,
                body = ByteArray(0).toRequestBody(null, 0)
        ) ?: return null

        return tokenResponseAdapter.fromJson(execute)?.token

    }

    suspend fun setStatus(full_repo: String, commit_id: String, status: GithubCommitStatus, installation: String) {

        val token = getToken(installation)

        val commitStatusJson = commitStatusAdapter.toJson(status)

        executePostCommand(
                command = "repos/$full_repo/statuses/$commit_id",
                token = token!!,
                body = commitStatusJson.toRequestBody(JSONMediaType)
        )

    }

    suspend fun addIssue(full_repo: String, status: GithubIssue, installation: String): String? {

        val token = getToken(installation)

        val issueJSON = githubIssueAdapter.toJson(status)

        return executePostCommand(
                command = "repos/$full_repo/issues",
                token = token!!,
                body = issueJSON.toRequestBody(JSONMediaType)
        )
    }

    suspend fun addLabel(full_repo: String, status: GithubLabel, installation: String): String? {

        val token = getToken(installation)

        val labelJSON = githubLabelAdapter.toJson(status)

        return executePostCommand(
                command = "repos/$full_repo/labels",
                token = token!!,
                body = labelJSON.toRequestBody(JSONMediaType)
        )
    }

    suspend fun addIssueComment(full_repo: String, issue: String, body: String, installation: String): String? {

        val token = getToken(installation)

        val bodyString = "{\"body\":\"$body\"}"

        return executePostCommand(
                command = "repos/$full_repo/issues/$issue/comments",
                token = token!!,
                body = bodyString.toRequestBody(JSONMediaType)
        )
    }


    suspend fun getUserPGP(user: String, installation: String): List<GithubPGPKeyInfo> {

        val token = getToken(installation)

        return githubPGPKeyInfoAdapter.fromJson(executeGetCommand(
                command = "users/$user/gpg_keys",
                token = token!!
        )!!)!!
    }

    protected suspend fun executePostCommand(command: String, token: String, body: RequestBody): String? {
        val request = Request.Builder()
                .post(body)
                .header("Authorization", "Bearer $token")
                .header("Accept", "application/vnd.github.machine-man-preview+json")
                .url("https://api.github.com/$command")
                .build()

        val execute = executeWithRetry(request)
        val res = execute?.body?.use { it.string() }

        if (execute == null || execute.code / 100 != 2) {
            println("problem executing $command $res")
            return null
        }

        return res
    }

    protected suspend fun executeGetCommand(command: String, token: String): String? {
        val request = Request.Builder()
                .get()
                .header("Authorization", "Bearer $token")
                .header("Accept", "application/vnd.github.machine-man-preview+json")
                .url("https://api.github.com/$command")
                .build()

        val response = executeWithRetry(request)
        val res = response?.body?.use { it.string() }

        if (response == null || response.code / 100 != 2) {
            println("problem executing $command $res")
            return null
        }

        return res
    }

    private suspend fun executeWithRetry(request: Request, attempts: Int = 5): Response? {
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