import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktorVersion = "1.2.4"
val jacksonVersion = "2.9.8"
val kafkaVersion = "2.3.0"
val flywayVersion = "6.0.0-beta"
val hikariVersion = "3.3.1"
val vaultJdbcVersion = "1.3.1"
val kotliqueryVersion = "1.3.0"
val junitJupiterVersion = "5.5.2"
val wireMockVersion = "2.23.2"
val mockkVersion = "1.9.3"
val micrometerRegistryPrometheusVersion = "1.1.5"
val mainClass = "no.nav.pensjon.saksbehandling.AppKt"

plugins {
    `build-scan`
    kotlin("jvm") version "1.3.50"
}

buildscript {
    dependencies {
        classpath("org.junit.platform:junit-platform-gradle-plugin:1.2.0")
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("net.logstash.logback:logstash-logback-encoder:5.2")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")

    implementation("io.ktor:ktor-metrics-micrometer:$ktorVersion")
    implementation("io.micrometer:micrometer-registry-prometheus:$micrometerRegistryPrometheusVersion")

    implementation("com.zaxxer:HikariCP:$hikariVersion")
    implementation("no.nav:vault-jdbc:$vaultJdbcVersion")
    implementation("com.github.seratch:kotliquery:$kotliqueryVersion")

    testImplementation("io.ktor:ktor-client-cio:$ktorVersion")

    testImplementation("org.testcontainers:testcontainers:1.12.3")
    testImplementation("org.testcontainers:oracle-xe:1.12.3")
    testImplementation("org.testcontainers:junit-jupiter:1.12.3")
    testImplementation("com.oracle.ojdbc:ojdbc8:19.3.0.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitJupiterVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
}


repositories {
    mavenCentral()
    maven("https://kotlin.bintray.com/ktor")
}

java {
    sourceCompatibility = JavaVersion.VERSION_12
    targetCompatibility = JavaVersion.VERSION_12
}

tasks.named<Jar>("jar") {
    baseName = "app"

    manifest {
        attributes["Main-Class"] = mainClass
        attributes["Class-Path"] = configurations.runtimeClasspath.get().joinToString(separator = " ") {
            it.name
        }
    }

    doLast {
        configurations.runtimeClasspath.get().forEach {
            val file = File("$buildDir/libs/${it.name}")
            if (!file.exists())
                it.copyTo(file)
        }
    }
}

tasks.named<KotlinCompile>("compileKotlin") {
    kotlinOptions.jvmTarget = "12"
}


tasks.named<KotlinCompile>("compileTestKotlin") {
    kotlinOptions.jvmTarget = "12"
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.withType<Wrapper> {
    gradleVersion = "5.6.4"
}