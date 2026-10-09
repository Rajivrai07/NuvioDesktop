plugins {
    java
    alias(libs.plugins.sentry.jvm.gradle)
}

val sentryAuthToken = providers.environmentVariable("SENTRY_AUTH_TOKEN")
    .orNull
    ?.trim()
    ?.takeIf { it.isNotBlank() && it != "placeholder" }

sentry {
    includeSourceContext.set(true)
    autoUploadSourceContext.set(sentryAuthToken != null)
    additionalSourceDirsForSourceContext.set(
        setOf(
            "../composeApp/src/commonMain/kotlin",
            "../composeApp/src/desktopMain/kotlin",
            "../composeApp/src/fullCommonMain/kotlin",
        ),
    )
    includeDependenciesReport.set(false)
    telemetry.set(false)
    org.set("nuviomedia")
    projectName.set("nuvio-desktop")
    sentryAuthToken?.let(authToken::set)
    autoInstallation {
        enabled.set(false)
    }
}

// Skip Sentry upload tasks when no valid token is configured.
if (sentryAuthToken == null) {
    tasks.matching { it.name.startsWith("sentryUpload") || it.name.startsWith("sentryCollect") || it.name.startsWith("sentryBundle") }.configureEach {
        enabled = false
    }
}
