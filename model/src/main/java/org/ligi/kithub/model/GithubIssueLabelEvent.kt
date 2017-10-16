package org.ligi.kithub.model

data class GithubIssueLabelEvent(val action: String,
                                 val issue: GithubIssue,
                                 val label: GithubLabel,
                                 val sender: GithubUser,
                                 val repository: GithubRepository,
                                 val installation: GithubInstallationInfo)