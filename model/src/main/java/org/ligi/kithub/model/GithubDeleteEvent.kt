package org.ligi.kithub.model

data class GithubDeleteEvent(val ref: String,
                             val repository: GithubRepository,
                             val installation: GithubInstallationInfo)