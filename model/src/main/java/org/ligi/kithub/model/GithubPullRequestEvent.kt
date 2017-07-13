package org.ligi.kithub.model

data class GithubPullRequestEvent(val action: String,
                                  val number: Int,
                                  val pull_request: GithubPullRequest,
                                  val installationInfo: GithubInstallationInfo)