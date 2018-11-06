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
package ru.surfstudio.android.picturechooser

import android.Manifest
import io.reactivex.Observable
import ru.surfstudio.android.core.ui.permission.PermissionManager
import ru.surfstudio.android.core.ui.permission.PermissionRequest

/**
 * утилита для проверки и запроса пермишенов для камеры и хранилища
 */
class PicturePermissionChecker(private val permissionManager: PermissionManager) {

    /**
     * проверка и запрос пермишена на камеру и хранилища
     */
    fun checkCameraStoragePermission(): Observable<Boolean> =
            permissionManager
                    .request(CameraStoragePermissionRequest())
                    .toObservable()

    /**
     * проверка и запрос пермишена на чтения из хранилища
     */
    fun checkGalleryStoragePermission(): Observable<Boolean> =
            permissionManager
                    .request(GalleryStoragePermissionRequest())
                    .toObservable()
}

/**
 * пермишен на запрос разрешения камеры
 */
class CameraStoragePermissionRequest : PermissionRequest() {

    override val permissions: Array<String>
        get() = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
}

/**
 * пермишен на запрос разрешения галереи
 */
class GalleryStoragePermissionRequest : PermissionRequest() {

    override val permissions: Array<String>
        get() = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
}

