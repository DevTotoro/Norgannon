plugins {
    alias(libs.plugins.nx.projectGraph)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency)
    alias(libs.plugins.ktlint)
}

group = "com.github.devtotoro.thevideoclub"
version = "0.0.1-SNAPSHOT"
description = "api"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Core Framework & Infrastructure
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.flyway)
    implementation(libs.spring.boot.starter.web)

    // Security & Identity
    implementation(libs.spring.boot.starter.oauth2.resource.server)

    // Database Core Adapters
    implementation(libs.flyway.database.postgresql)
    runtimeOnly(libs.postgresql)

    // Kotlin Extensibility & Custom Utilities
    implementation(libs.kotlin.reflect)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.tsid.creator)

    // Development Profiling tools
    developmentOnly(libs.spring.boot.devtools)
    annotationProcessor(libs.spring.boot.configuration.processor)

    // Engineering Test Harnesses
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.security.test)
    testImplementation(libs.kotlin.test.junit5)
    testRuntimeOnly(libs.junit.platform.launcher)

    // Testcontainers Infrastructure
    testImplementation(platform(libs.testcontainers.bom))
    testImplementation(libs.spring.boot.testcontainers)
    testImplementation(libs.testcontainers.postgresql)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()

    jvmArgs("-XX:+EnableDynamicAgentLoading", "--enable-native-access=ALL-UNNAMED")
}

allprojects {
    apply {
        plugin(
            libs.plugins.nx.projectGraph
                .get()
                .pluginId,
        )
    }
}
