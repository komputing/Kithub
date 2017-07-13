package org.ligi.kithub.model

data class GithubIssue(val title: String,
                       val body: String,
                       val milestone: String,
                       val labels:List<String>,
                       val assignees: List<String>
)