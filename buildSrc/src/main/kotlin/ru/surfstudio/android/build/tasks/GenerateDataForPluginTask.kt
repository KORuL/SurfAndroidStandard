package ru.surfstudio.android.build.tasks

import com.google.gson.GsonBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import ru.surfstudio.android.build.Components
import java.io.File

/**
 * Task for generating data about versions to JSON file for version plugin
 * Do this task before deploy Android-Standard version plugin
 */
open class GenerateDataForPluginTask : DefaultTask() {

    companion object {
        private const val PLUGIN_MODULE = "android-standard-version-plugin"
        private const val OUTPUT_DIR = "src/main/resources"
        private const val FILE_NAME = "versions.json"
    }

    @TaskAction
    fun generate() {
        val gson = GsonBuilder().create()
        val jsonText = gson.toJson(Components.libraries.map { it.name to it.projectVersion })
        File("$PLUGIN_MODULE/$OUTPUT_DIR/$FILE_NAME").writeText(jsonText)
    }
}