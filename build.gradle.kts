import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.0.1"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
    id("jacoco")
    id("org.jetbrains.kotlin.kapt") version "1.2.71"
    id("org.sonarqube") version "3.5.0.2730"
}

version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
    forEach {
        it.exclude(group = "com.vaadin.external.google", module = "android-json")
    }
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

extra["springCloudVersion"] = "2022.0.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.cloud:spring-cloud-starter")
    implementation("org.springframework.cloud:spring-cloud-starter-loadbalancer")
    implementation("org.springframework.session:spring-session-core")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("com.mysql:mysql-connector-j")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
    testImplementation("org.springframework.security:spring-security-test:6.0.2")
    testImplementation("net.bytebuddy:byte-buddy:1.14.2")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
    kapt("org.seasar.doma:doma:2.24.0")
    implementation("org.seasar.doma:doma:2.24.0")
    implementation("org.seasar.doma.boot:doma-spring-boot-starter:1.7.0")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("org.springframework.security:spring-security-config:6.0.2")
    implementation("org.springframework.security:spring-security-web:6.0.2")
    implementation("com.auth0:java-jwt:4.3.0")
    implementation("io.lettuce:lettuce-core:6.2.3.RELEASE")
}

sonarqube {
    properties {
        property("sonar.projectKey", "yuta-komura_ddd-practice")
        property("sonar.organization", "yuta-komura")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.exclusions", "src/test/**")
        property("sonar.exclusions", "src/**/Application.kt")
    }
}

val compileKotlin: KotlinCompile by tasks

kapt {
    arguments {
        arg("doma.resources.dir", compileKotlin.destinationDirectory.get())
    }
}

tasks.register("copyDomaResources", Sync::class) {
    from("src/main/resources")
    into(compileKotlin.destinationDirectory.get())
    include("doma.compile.config")
    include("META-INF/**/*.sql")
    include("META-INF/**/*.script")
}

tasks.withType<KotlinCompile> {
    dependsOn(tasks.getByName("copyDomaResources"))
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.named<Test>("test") {
    jvmArgs("-Xshare:off")
    useJUnitPlatform()
    finalizedBy("jacocoTestReport")
}

tasks.named<JacocoReport>("jacocoTestReport") {
    reports {
        xml.required.set(false)
        html.required.set(true)
    }
}