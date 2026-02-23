plugins {
    `java-library`
    `maven-publish`
    signing
}

group = "dev.postproxy"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
}

dependencies {
    api("com.fasterxml.jackson.core:jackson-databind:2.17.2")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.2")

    testImplementation("org.junit.jupiter:junit-jupiter:5.11.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

tasks.javadoc {
    (options as StandardJavadocDocletOptions).addBooleanOption("Xdoclint:none", true)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            pom {
                name.set("Postproxy Java SDK")
                description.set("Java SDK for the Postproxy API")
                url.set("https://postproxy.dev")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        name.set("Postproxy")
                        email.set("contact@postproxy.dev")
                        organization.set("Postproxy")
                        organizationUrl.set("https://postproxy.dev")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/postproxy/postproxy-java.git")
                    developerConnection.set("scm:git:ssh://github.com:postproxy/postproxy-java.git")
                    url.set("https://github.com/postproxy/postproxy-java")
                }
            }
        }
    }
    repositories {
        maven {
            name = "staging"
            url = uri(layout.buildDirectory.dir("staging-deploy"))
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications["maven"])
}
