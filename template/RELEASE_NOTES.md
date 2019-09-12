[TOC]
# Template Release Notes
## 0.5.0-alpha
##### Template
* ANDDEP-329 Added RxJava2Debug
* TODO

## 0.4.0
##### Template
* Updated navigation mechanisms in template
* ANDDEP-323 Added Chuck
* ANDDEP-336 Added TinyDancer
* ANDDEP-335 Added Stetho
* Added version labels on application icons in the launcher
* Добавлен переключатель между основным и тестовым сервером
* Added switch between main and test server
* Added LeakCanary
* Added viewer of file storage of application
* Added plugin [`Build scans`](https://guides.gradle.org/creating-build-scans/)
* Added the ability to add a request execution delay
    * On the DebugScreen screen in the server settings section, you can add a request delay of 0s, 0.5s, 1s, 2s, 4s, 8s
* ANDDEP-444 Dagger dependencies taken out of [`AppComponent`](template/base_feature/src/main/java/ru/surfstudio/standard/application/app/di/AppComponent.kt)
and [`ActivityComponent`](template/base_feature/src/main/java/ru/surfstudio/standard/ui/activity/di/ActivityComponent.kt)
in separate classes:  [`AppProxyDependencies`](template/base_feature/src/main/java/ru/surfstudio/standard/application/app/di/AppProxyDependencies.kt)
and [`ActivityProxyDependencies`](template/base_feature/src/main/java/ru/surfstudio/standard/ui/activity/di/ActivityProxyDependencies.kt),
which are now responsible for distributing dependencies between components.
* SBB-1862 Added module cf-pagination

## 0.3.0
##### Template
* ANDDEP-272 DebugScreen
* ANDDEP-250 Assembly types are moved to a separate gradle file
* ANDDEP-254 Added application signature mechanism - keystore directory.
* ANDDEP-255 Created a minimal test environment for testing without an emulator (Robolectric)
* Added the ability to connect modules locally. Description is [here](template/android-standard/README.md)