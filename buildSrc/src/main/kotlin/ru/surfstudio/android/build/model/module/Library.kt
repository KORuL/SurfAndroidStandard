package ru.surfstudio.android.build.model.module

import ru.surfstudio.android.build.EMPTY_STRING
import ru.surfstudio.android.build.model.dependency.AndroidStandardDependency
import ru.surfstudio.android.build.model.dependency.Dependency
import ru.surfstudio.android.build.model.dependency.ThirdPartyDependency

/**
 * Represent information about library
 */
class Library(
        override val name: String = EMPTY_STRING,
        override val directory: String = EMPTY_STRING,
        val artifactName: String = EMPTY_STRING,
        val thirdPartyDependencies: List<ThirdPartyDependency> = listOf(),
        val androidStandardDependencies: List<AndroidStandardDependency> = listOf()
) : Module(name, directory)