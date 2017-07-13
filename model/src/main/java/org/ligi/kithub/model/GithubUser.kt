package org.ligi.kithub.model

data class GithubUser(val name: String?, val email: String?, val username: String?, val login: String?) {
    fun safeName() = login ?: name ?: username
}