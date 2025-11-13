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
    mainClass.set("org.Main")
}

tasks.generateGrammarSource {
    maxHeapSize = "64m"
    arguments = listOf("-visitor", "-listener")
    outputDirectory = file("build/generated-src/antlr/main/org/antlr/generated")
}

tasks.test {
    useJUnitPlatform()
}
