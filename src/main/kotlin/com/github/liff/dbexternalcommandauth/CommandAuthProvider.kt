package com.github.liff.dbexternalcommandauth

import com.intellij.database.access.DatabaseCredentials
import com.intellij.database.dataSource.DatabaseAuthProvider
import com.intellij.database.dataSource.DatabaseConnectionConfig
import com.intellij.database.dataSource.DatabaseConnectionInterceptor.ProtoConnection
import com.intellij.database.dataSource.DatabaseConnectionPoint
import com.intellij.database.dataSource.DatabaseCredentialsAuthProvider.Companion.applyCredentials
import com.intellij.database.dataSource.ui.AuthWidgetBuilder
import com.intellij.database.view.DatabaseCoreUiService
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir

class CommandAuthProvider : DatabaseAuthProvider {
    override suspend fun interceptConnection(
        proto: ProtoConnection,
        silent: Boolean,
    ): Boolean {
        val command =
            proto.connectionPoint.getAdditionalProperty("command")
                ?: error(MyBundle.message("no-command"))

        val directory = proto.project.guessProjectDir()?.let {
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

    override fun createWidget(
        project: Project?,
        credentials: DatabaseCredentials,
        config: DatabaseConnectionConfig,
    ): DatabaseAuthProvider.AuthWidget? =
        DatabaseCoreUiService
            .getInstance()
            .createAuthWidgetBuilder()
            ?.apply {
                addTextField(
                    MyBundle.messagePointer("command"),
                    AuthWidgetBuilder.additionalPropertySerializer("command"),
                    AuthWidgetBuilder.removeParameterHandler("command"),
                )
            }?.build(project, credentials, config)

    override fun getId(): String = "external-command"

    override fun getDisplayName(): String = MyBundle.message("name")
}
