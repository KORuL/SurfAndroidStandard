/*
  Copyright (c) 2018-present, SurfStudio LLC, Maxim Tuev.

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
package ru.surfstudio.android.mvp.widget.delegate.factory;

import androidx.annotation.NonNull;
import android.view.View;

import java.util.List;

import ru.surfstudio.android.core.ui.event.ScreenEventResolverHelper;
import ru.surfstudio.android.core.ui.event.base.resolver.ScreenEventResolver;
import ru.surfstudio.android.core.ui.scope.PersistentScopeStorage;
import ru.surfstudio.android.core.ui.scope.PersistentScopeStorageContainer;
import ru.surfstudio.android.mvp.widget.delegate.ParentPersistentScopeFinder;
import ru.surfstudio.android.mvp.widget.delegate.WidgetViewDelegate;
import ru.surfstudio.android.mvp.widget.view.CoreWidgetViewInterface;


/**
 * Фабрика делегатов виджетов по умолчанию, предоставляет стандартные делегаты
 */
public class DefaultMvpWidgetDelegateFactory implements MvpWidgetDelegateFactory {

    @Override
    public <W extends View & CoreWidgetViewInterface> WidgetViewDelegate createWidgetViewDelegate(W widget) {
        PersistentScopeStorage scopeStorage = getScopeStorage();
        return new WidgetViewDelegate(
                widget,
                scopeStorage,
                new ParentPersistentScopeFinder(widget, scopeStorage));
    }

    @NonNull
    protected List<ScreenEventResolver> getEventResolvers() {
        return ScreenEventResolverHelper.standardEventResolvers();
    }

    @NonNull
    protected PersistentScopeStorage getScopeStorage() {
        return PersistentScopeStorageContainer.getPersistentScopeStorage();
    }
}
