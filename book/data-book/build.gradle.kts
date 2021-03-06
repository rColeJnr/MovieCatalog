apply {
    from("$rootDir/kotlin-library.gradle")
    plugin("kotlin-kapt")
    plugin("kotlin-parcelize")
    plugin("dagger.hilt.android.plugin")
}

dependencies {

    "implementation"(project(":core"))

    "implementation"(Retrofit.retrofit)
    "implementation"(Retrofit.converter)
    "implementation"(Retrofit.okhttp)
    "implementation"(Retrofit.interceptor)

    "implementation"(Hilt.android)
    "kapt"(Hilt.compiler)

    "implementation"(Room.roomRuntime)

    "implementation"(Coroutines.core)
}