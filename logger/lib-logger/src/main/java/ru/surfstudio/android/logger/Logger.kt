/*
  Copyright (c) 2018-present, SurfStudio LLC.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package ru.surfstudio.android.logger

import android.util.Log
import ru.surfstudio.android.logger.logging_strategies.LoggingStrategy
import kotlin.reflect.KClass

/**
 * Object for logging with using different levels and strategies
 */
object Logger {

    private val LOGGING_STRATEGIES = hashMapOf<KClass<*>, LoggingStrategy>()
    private val LOGGING_DEFINED_PRIORITIES = hashMapOf<KClass<*>, Int>()

    @JvmStatic
    fun getLoggingStrategies() = LOGGING_STRATEGIES

    @JvmStatic
    fun getDefaultLoggingPriorities() = LOGGING_DEFINED_PRIORITIES

    @JvmStatic
    fun addLoggingStrategy(strategy: LoggingStrategy) = LOGGING_STRATEGIES.put(strategy::class, strategy)

    @JvmStatic
    fun removeLoggingStrategy(strategy: LoggingStrategy) = LOGGING_STRATEGIES.remove(strategy::class)

    @JvmStatic
    fun addDefalutLoggingPriority(errorClass: KClass<*>, defaultPriority: Int) = LOGGING_DEFINED_PRIORITIES.put(errorClass, defaultPriority)

    @JvmStatic
    fun removeDefalutLoggingPriority(errorClass: KClass<*>) = LOGGING_STRATEGIES.remove(errorClass)

    /**
     * Log a verbose developerMessage with optional format args.
     */
    @JvmStatic
    fun v(message: String, vararg args: Any) {
        log(Log.VERBOSE, null, message, *args)
    }

    /**
     * Log a verbose exception and a developerMessage with optional format args.
     */
    @JvmStatic
    fun v(t: Throwable?, message: String, vararg args: Any) {
        log(Log.VERBOSE, t, message, *args)
    }

    /**
     * Log a debug developerMessage with optional format args.
     */
    @JvmStatic
    fun d(message: String, vararg args: Any) {
        log(Log.DEBUG, null, message, *args)
    }

    /**
     * Log a debug exception and a developerMessage with optional format args.
     */
    @JvmStatic
    fun d(t: Throwable?, message: String, vararg args: Any) {
        log(Log.DEBUG, t, message, *args)
    }

    /**
     * Log an info developerMessage with optional format args.
     */
    @JvmStatic
    fun i(message: String, vararg args: Any) {
        log(Log.INFO, null, message, *args)
    }

    /**
     * Log an info exception and a developerMessage with optional format args.
     */
    @JvmStatic
    fun i(t: Throwable?, message: String, vararg args: Any) {
        log(Log.INFO, t, message, *args)
    }

    /**
     * Log an expectable error.
     */
    @JvmStatic
    fun w(t: Throwable?) {
        log(Log.WARN, t, null)
    }

    /**
     * Log a warning developerMessage with optional format args.
     */
    @JvmStatic
    fun w(message: String, vararg args: Any) {
        log(Log.WARN, null, message, *args)
    }

    /**
     * Log a warning exception and a developerMessage with optional format args.
     */
    @JvmStatic
    fun w(t: Throwable?, message: String, vararg args: Any) {
        log(Log.WARN, t, message, *args)
    }

    /**
     * Log an error developerMessage with optional format args.
     */
    @JvmStatic
    fun e(message: String, vararg args: Any) {
        log(Log.ERROR, null, message, *args)
    }

    /**
     * Log an error exception and a developerMessage with optional format args.
     */
    @JvmStatic
    fun e(t: Throwable?, message: String, vararg args: Any) {
        log(Log.ERROR, t, message, *args)
    }

    @JvmStatic
    fun e(t: Throwable?) {
        log(Log.ERROR, t, null)
    }

    private fun log(priority: Int, t: Throwable?, message: String?, vararg args: Any?) {
        forEachLoggingStrategy { strategy -> strategy.log(getDefinedPriority(t, priority), t, message, args) }
    }

    private fun forEachLoggingStrategy(action: (LoggingStrategy) -> Unit) {
        LOGGING_STRATEGIES.values.forEach(action)
    }

    private fun getDefinedPriority(t: Throwable?, defaultPriority: Int): Int =
            when {
                t != null -> LOGGING_DEFINED_PRIORITIES[t::class] ?: defaultPriority
                else -> defaultPriority
            }
}