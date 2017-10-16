package org.ligi.kithub.model

data class GithubBaseEvent(val action: String,
                           val installationInfo: GithubInstallationInfo)