import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    base
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.serialization)

    id("java-library")
    id("maven-publish")
    publishing
    signing

    id("jacoco-report-aggregation")
}

jacoco {
    toolVersion = "0.8.8"
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "jacoco")

    repositories {
        mavenCentral()
        mavenLocal()
        google()
    }

    group = "cc.unitmesh"
    version = "0.4.0"

    java.sourceCompatibility = JavaVersion.VERSION_11
    java.targetCompatibility = JavaVersion.VERSION_11

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    tasks.test {
        finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
    }

    tasks.jacocoTestReport {
        dependsOn(tasks.test) // tests are required to run before generating the report
    }

    tasks.jacocoTestReport {
        reports {
            xml.required.set(true)
            csv.required.set(false)
            html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
        }
    }
}

configure(allprojects
    - project(":")
    - project(":examples")
    - project(":examples:project-example")
    - project(":unit-gen")
) {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")
    apply(plugin = "publishing")

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
                versionMapping {
                    usage("java-api") {
                        fromResolutionOf("runtimeClasspath")
                    }
                    usage("java-runtime") {
                        fromResolutionResult()
                    }
                }
                pom {
                    name.set("UnitGen")
                    description.set("Unit Mesh Eval")
                    url.set("https://github.com/unit-mesh/")
                    licenses {
                        license {
                            name.set("MIT")
                            url.set("https://raw.githubusercontent.com/unit-mesh/unit-gen/master/LICENSE")
                        }
                    }
                    licenses {
                        license {
                            name.set("MIT")
                            url.set("https://github.com/unit-mesh/unit-gen/raw/master/LICENSE")
                        }
                    }
                    developers {
                        developer {
                            id.set("Unit Mesh")
                            name.set("UnitMesh Team")
                            email.set("h@phodal.com")
                        }
                    }
                    scm {
                        connection.set("scm:git:git://github.com/unit-mesh/unit-gen.git")
                        developerConnection.set("scm:git:ssh://github.com/unit-mesh/unit-gen.git")
                        url.set("https://github.com/unit-mesh/unit-gen/")
                    }
                }
            }
        }

        repositories {
            maven {
                val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl

                credentials {
                    username = (
                            if (project.findProperty("sonatypeUsername") != null) project.findProperty("sonatypeUsername") else System.getenv(
                                "MAVEN_USERNAME"
                            )).toString()
                    password = (
                            if (project.findProperty("sonatypePassword") != null) project.findProperty("sonatypePassword") else System.getenv(
                                "MAVEN_PASSWORD"
                            )).toString()
                }
            }
        }
    }

    signing {
        sign(publishing.publications["mavenJava"])
    }

    java {
        withJavadocJar()
        withSourcesJar()
    }
}

