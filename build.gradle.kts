// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    extra.apply {
        // Sdk and tools
        this["compileSdkVersion"] = 28
        this["minSdkVersion"] = 21
        this["targetSdkVersion"] = 28
        this["forecastKey"] = "7e833141fef43d5cb7c86f7ad280e3fe"
        // App dependencies
        set("gradleVersion", "3.4.2")
        set("kotlinVersion", "1.3.31")
        set("daggerVersion", "2.23.2")
        set("okhttpVersion", "3.14.1")
        set("lifecycleVersion", "2.2.0-alpha01")
        set("coroutinesVersion", "1.1.1")
        set("playServicesVersion", "16.0.0")
        set("recyclerViewVersion", "1.1.0-alpha05")
        set("cardViewVersion", "1.0.0")
        set("supportLibraryVersion", "1.1.0-alpha05")
        set("constraintLayoutVersion", "2.0.0-beta1")
        set("ktxVersion", "1.2.0-alpha01")
        set("navigationVersion", "2.0.0")
        // Tests
        set("junitVersion", "4.13-beta-2")
        set("mockkVersion", "1.9.3")
        set("archCoreVersion", "1.1.1")
        set("coroutineTestVersion", "1.2.2")
    }
    repositories {
        google()
        jcenter()

    }
    dependencies {
        val gradleVersion: String by extra
        val kotlinVersion: String by extra

        classpath("com.android.tools.build:gradle:$gradleVersion")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        jcenter()

    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

tasks.register("test", Test::class) {
    testLogging {
        events("PASSED", "FAILED", "SKIPPED", "standardOut", "standardError")
    }
}
