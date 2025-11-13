plugins {
    id("java")
    id("application")
    id("antlr")
}

group = "org"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    antlr("org.antlr:antlr4:4.13.2")
    implementation("org.antlr:antlr4-runtime:4.13.2")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    mainClass.set("org.MainWithUi")
}

tasks.generateGrammarSource {
    maxHeapSize = "64m"
    arguments = listOf("-visitor", "-listener")
    outputDirectory = file("build/generated-src/antlr/main/org/antlr/generated")
}

tasks.register<JavaExec>("runWithUi") {
    group = "application"
    description = "Lance le programme avec interface"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("org.MainWithUI")
}

tasks.register<JavaExec>("runWithoutUi") {
    group = "application"
    description = "Lance le programme sans interface"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("org.MainWithoutUI")
}

tasks.test {
    useJUnitPlatform()
}
