import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.0.1"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
    id("jacoco")
    id("org.jetbrains.kotlin.kapt") version "1.2.71"
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
    kapt("org.seasar.doma:doma:2.24.0")
    implementation("org.seasar.doma:doma:2.24.0")
    implementation("org.seasar.doma.boot:doma-spring-boot-starter:1.7.0")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("org.springframework.boot:spring-boot-starter-redis:1.4.7.RELEASE")
    implementation("org.springframework.data:spring-data-redis:3.0.0")
    implementation("org.springframework.security:spring-security-config:6.0.1")
    implementation("org.springframework.security:spring-security-web:6.0.1")
    implementation("com.auth0:java-jwt:4.2.1")
    implementation("org.codehaus.mojo:aspectj-maven-plugin:1.14.0")
    implementation("org.springframework:spring-aspects:3.2.4.RELEASE")
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

tasks.withType<Test> {
    useJUnitPlatform()
}
