package org.ligi.kithub.model

data class GithubIssue(val title: String,
                       val body: String,
                       val user: GithubUser,
                       val milestone: String?,
                       val pullRequest: GithubPullRequest?,
                       val labels:List<String>,
                       val assignees: List<String>
)