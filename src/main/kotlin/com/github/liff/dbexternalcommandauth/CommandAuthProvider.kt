package com.github.liff.dbexternalcommandauth

import com.intellij.database.dataSource.DatabaseAuthProvider
import com.intellij.database.dataSource.DatabaseAuthProviderParam
import com.intellij.database.dataSource.DatabaseAuthParameterBuilderHelper
import com.intellij.database.dataSource.DatabaseConnectionConfig
import com.intellij.database.dataSource.DatabaseConnectionInterceptor.ProtoConnection
import com.intellij.database.dataSource.DatabaseConnectionPoint
import com.intellij.database.dataSource.DatabaseCredentialsAuthProvider.Companion.applyCredentials
import com.intellij.openapi.project.guessProjectDir

class CommandAuthProvider : DatabaseAuthProvider {
    override suspend fun interceptConnection(
        proto: ProtoConnection,
        silent: Boolean,
    ): Boolean {
        val command =
            proto.connectionPoint.getAdditionalProperty("command")
                ?: error(MyBundle.message("no-command"))

        val directory =
            proto.project.guessProjectDir()?.let {
                try {
                    it.toNioPath().toFile()
                } catch (_: UnsupportedOperationException) {
                    null
                }
            }

        val credentials = acquire(command = command, directory = directory)

        applyCredentials(
            proto = proto,
            credentials = credentials,
            cancelOnEmpty = true,
        )

        return true
    }

    override fun getApplicability(
        point: DatabaseConnectionPoint,
        level: DatabaseAuthProvider.ApplicabilityLevel,
    ): DatabaseAuthProvider.ApplicabilityLevel.Result = DatabaseAuthProvider.ApplicabilityLevel.Result.APPLICABLE

    override fun getAuthParams(
        config: DatabaseConnectionConfig,
    ): List<DatabaseAuthProviderParam<*>> =
        DatabaseAuthProviderParam.buildParams {
            textParam(
                id = "command",
                label = MyBundle.message("command"),
                serializer = DatabaseAuthParameterBuilderHelper.additionalPropertySerializer("command"),
                urlHandler = DatabaseAuthParameterBuilderHelper.removeParameterHandler("command"),
            )
        }

    override fun getId(): String = "external-command"

    override fun getDisplayName(): String = MyBundle.message("name")
}
