package com.github.liff.dbexternalcommandauth

import com.intellij.credentialStore.Credentials
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
private data class VaultLike(val data: Data) {
    @Serializable
    data class Data(val username: String, val password: String)
}

fun parse(output: String): Credentials? {
    val vaultLike =
        runOrNull { Json.decodeFromString<VaultLike>(output) }
            ?.let { Credentials(it.data.username, it.data.password) }

    fun passLike() =
        runOrNull {
            var (password, username) = output.lines()
            if (username.lowercase().startsWith("username:")) {
                username = username.substring(9).trimStart()
            }
            Credentials(username, password)
        }

    return vaultLike ?: passLike()
}

private inline fun <T : Any> runOrNull(f: () -> T): T? =
    try {
        f()
    } catch (_: RuntimeException) {
        null
    }
