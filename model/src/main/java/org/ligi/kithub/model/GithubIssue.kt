package org.ligi.kithub.model

data class GithubIssue(val title: String,
                       val body: String,
                       val user: GithubUser,
                       val number: Long,
                       val milestone: String?,
                       val pull_request: GithubPullRequest?,
                       val labels:List<GithubLabel>,
                       val assignees: List<String>
)