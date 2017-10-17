package org.ligi.kithub.model

data class GithubPGPKeyInfo(val id: Long,
                            val key_id: String,
                            val public_key: String,
                            val created_at: String,
                            val expires_at: String)