package org.ligi.kithub.model

data class GithubIssueCloseEvent(val action: String,
                                 val issue: GithubIssue,
                                 val sender: GithubUser,
                                 val repository: GithubRepository,
                                 val installation: GithubInstallationInfo)