package com.github.liff.dbexternalcommandauth

import com.intellij.credentialStore.Credentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun acquire(
    command: String,
    parse: (String) -> Credentials? = ::parse,
): Result<Credentials?> =
    withContext(Dispatchers.IO) {
        val proc = wrap(command).start()

        proc.outputStream.close()

        val output = proc.inputStream.readNBytes(MAX_OUTPUT_SIZE).decodeToString()
        val errors = proc.errorStream.readNBytes(MAX_OUTPUT_SIZE).decodeToString()
        if (proc.waitFor() == 0) {
            Result.success(parse(output))
        } else {
            Result.failure(RuntimeException(errors))
        }
    }

private const val MAX_OUTPUT_SIZE = 32 * 1024

private fun wrap(command: String): ProcessBuilder {
    val osName = System.getProperty("os.name")
    return if (osName.startsWith("Windows")) {
        ProcessBuilder("cmd.exe", "/c", command)
    } else {
        ProcessBuilder("sh", "-c", command)
    }
}
