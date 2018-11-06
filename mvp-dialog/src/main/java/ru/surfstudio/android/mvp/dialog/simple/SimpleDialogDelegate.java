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
package ru.surfstudio.android.mvp.dialog.simple;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import ru.surfstudio.android.core.mvp.configurator.ViewConfigurator;
import ru.surfstudio.android.core.mvp.scope.ActivityViewPersistentScope;
import ru.surfstudio.android.core.mvp.scope.FragmentViewPersistentScope;
import ru.surfstudio.android.core.ui.ScreenType;
import ru.surfstudio.android.core.ui.scope.PersistentScopeStorage;
import ru.surfstudio.android.core.ui.scope.PersistentScopeStorageContainer;
import ru.surfstudio.android.core.ui.scope.ScreenPersistentScope;
import ru.surfstudio.android.logger.LogConstants;
import ru.surfstudio.android.logger.RemoteLogger;
import ru.surfstudio.android.mvp.widget.scope.WidgetViewPersistentScope;

/**
 * delegate для {@link CoreSimpleDialogInterface}
 */

public class SimpleDialogDelegate {
    private static final String EXTRA_PARENT_TYPE = "EXTRA_PARENT_TYPE";
    private static final String EXTRA_PARENT_NAME = "EXTRA_PARENT_NAME";

    private ScreenType parentType;
    private String parentScopeId;

    private CoreSimpleDialogInterface simpleDialog;
    private DialogFragment dialogFragment;

    public <D extends DialogFragment & CoreSimpleDialogInterface> SimpleDialogDelegate(D simpleDialog) {
        this.simpleDialog = simpleDialog;
        this.dialogFragment = simpleDialog;
    }

    public <A extends ActivityViewPersistentScope> void show(A parentActivityViewPersistentScope) {
        show(parentActivityViewPersistentScope.getScreenState().getActivity().getSupportFragmentManager(),
                ScreenType.ACTIVITY,
                parentActivityViewPersistentScope.getScopeId());
    }

    public <F extends FragmentViewPersistentScope> void show(F parentFragmentViewPersistentScope) {
        show(parentFragmentViewPersistentScope.getScreenState().getFragment().getFragmentManager(),
                ScreenType.FRAGMENT,
                parentFragmentViewPersistentScope.getScopeId());
    }

    public <W extends WidgetViewPersistentScope> void show(W parentWidgetViewPersistentScope) {
        show(((FragmentActivity) parentWidgetViewPersistentScope.getScreenState().getWidget().getContext())
                        .getSupportFragmentManager(),
                ScreenType.WIDGET,
                parentWidgetViewPersistentScope.getScopeId());
    }

    protected void show(FragmentManager fragmentManager, ScreenType parentType, String parentName) {
        this.parentScopeId = parentName;
        this.parentType = parentType;
        dialogFragment.show(fragmentManager, simpleDialog.getName());
    }

    public <T> T getScreenComponent(Class<T> componentClass) {
        PersistentScopeStorage scopeStorage = PersistentScopeStorageContainer.getPersistentScopeStorage();
        ScreenPersistentScope persistentScope = (ScreenPersistentScope) scopeStorage.get(parentScopeId);
        ViewConfigurator viewConfigurator = (ViewConfigurator) persistentScope.getConfigurator();
        return componentClass.cast(viewConfigurator.getScreenComponent());
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (parentType == null && savedInstanceState != null) {
            parentType = (ScreenType) savedInstanceState.getSerializable(EXTRA_PARENT_TYPE);
            parentScopeId = savedInstanceState.getString(EXTRA_PARENT_NAME);
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(EXTRA_PARENT_TYPE, parentType);
        outState.putString(EXTRA_PARENT_NAME, parentScopeId);
    }

    public void onResume() {
        RemoteLogger.logMessage(String.format(LogConstants.LOG_DIALOG_RESUME_FORMAT, simpleDialog.getName()));
    }

    public void onPause() {
        RemoteLogger.logMessage(String.format(LogConstants.LOG_DIALOG_PAUSE_FORMAT, simpleDialog.getName()));
    }
}
