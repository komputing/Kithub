package org.ligi.kithub

import com.squareup.moshi.Types
import org.ligi.kithub.model.GithubRepository

open class GithubOrgAPI : GithubAPI() {

    private var repositoryListType = Types.newParameterizedType(List::class.java, GithubRepository::class.java)
    private var repositoryListAdapter = moshi.adapter<List<GithubRepository>>(repositoryListType)

    suspend fun listRepos(user: String): List<GithubRepository> {
        return repositoryListAdapter.fromJson(
            executeGetCommand("users/$user/repos")!!
        )!!

    }

}