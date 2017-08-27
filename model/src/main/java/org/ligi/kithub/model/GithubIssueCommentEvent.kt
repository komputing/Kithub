package org.ligi.kithub.model

data class GithubIssueCommentEvent(val action: String,
                                   val issue: GithubIssue,
                                   val comment: GithubComment,
                                   val sender: GithubUser,
                                   val repository: GithubRepository,
                                   val installation: GithubInstallationInfo)