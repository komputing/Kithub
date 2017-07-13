package org.ligi.kithub.model

data class GithubPullRequest(val url: String,
                             val repo: GithubRepository,
                             val head: GithubCommit?)