plugins {
    id("org.jetbrains.kotlin.jvm")
}

// Pure Kotlin module: no Android SDK, no framework dependencies.
// Keeps business rules testable without an emulator/Robolectric.
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    testImplementation("junit:junit:4.13.2")
}
