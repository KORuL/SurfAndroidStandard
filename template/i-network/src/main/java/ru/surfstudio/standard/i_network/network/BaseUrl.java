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
package ru.surfstudio.standard.i_network.network;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Класс для описания базовой части Url сервера api
 */
@Deprecated
public class BaseUrl {
    private static final String START_SLASH = "^/";
    private static final String END_SLASH = "/$";
    @NonNull
    private String base;
    @Nullable
    private String apiVersion;
    private int port;

    public BaseUrl(@NonNull String base, @Nullable String apiVersion, int port) {
        this.base = base.replaceFirst(END_SLASH, "");
        if (apiVersion != null) {
            this.apiVersion = apiVersion.replaceFirst(START_SLASH, "")
                    .replaceFirst(END_SLASH, "");
        }
        this.port = port;
    }

    @NonNull
    public String getBase() {
        return base;
    }

    @Nullable
    public String getApiVersion() {
        return apiVersion;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        String result = base;
        if (port > 0) {
            result += ":" + port;
        }
        if (apiVersion != null) {
            result += "/" + apiVersion + "/";
        }
        return result;
    }
}
