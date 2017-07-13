package org.ligi.kithub.model

data class GithubRepository(val id: Long, val name: String, val full_name: String, val owner: GithubUser)