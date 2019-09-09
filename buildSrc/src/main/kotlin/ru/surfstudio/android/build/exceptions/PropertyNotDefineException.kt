package ru.surfstudio.android.build.exceptions

import org.gradle.api.GradleException

/**
 * Throw when task need property
 */
class PropertyNotDefineException(propertyName: String) : GradleException(
        "Need define property $propertyName"
)