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
package ru.surfstudio.android.mvp.widget.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import ru.surfstudio.android.core.mvp.presenter.CorePresenter;
import ru.surfstudio.android.mvp.widget.R;
import ru.surfstudio.android.mvp.widget.delegate.WidgetViewDelegate;
import ru.surfstudio.android.mvp.widget.delegate.factory.MvpWidgetDelegateFactoryContainer;
import ru.surfstudio.android.mvp.widget.scope.WidgetViewPersistentScope;

/**
 * базовый класс для кастомной вьюшки с презентером, основанном на [LinearLayout]
 * <p>
 * !!!ВАЖНО!!!
 * Пока нельзя использовать в ресайклере
 */
public abstract class CoreLinearLayoutView extends LinearLayout implements CoreWidgetViewInterface {

    private WidgetViewDelegate widgetViewDelegate;
    private boolean isManualInitEnabled;

    public CoreLinearLayoutView(Context context, boolean isManualInitEnabled) {
        super(context, null);
        this.isManualInitEnabled = isManualInitEnabled;
        initWidgetViewDelegate();
    }

    public CoreLinearLayoutView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public CoreLinearLayoutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        obtainAttrs(attrs);
        initWidgetViewDelegate();
    }

    @TargetApi(21)
    public CoreLinearLayoutView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        obtainAttrs(attrs);
        initWidgetViewDelegate();
    }

    @SuppressLint("CustomViewStyleable")
    private void obtainAttrs(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CoreWidgetViewInterface, -1, -1);
        isManualInitEnabled = ta.getBoolean(R.styleable.CoreWidgetViewInterface_enableManualInit, false);
        ta.recycle();
    }

    protected abstract CorePresenter[] getPresenters();

    @Override
    public void bindPresenters() {
        for (CorePresenter presenter : getPresenters()) {
            presenter.attachView(this);
        }
    }

    @Override
    public WidgetViewDelegate createWidgetViewDelegate() {
        return MvpWidgetDelegateFactoryContainer.get().createWidgetViewDelegate(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isManualInitEnabled) {
            widgetViewDelegate.onCreate();
        }
    }

    @Override
    @Deprecated
    public void init() {}

    @Override
    public void init(String scopeId) {}

    @Override
    public String getWidgetId() {
        return Integer.toString(getId());
    }

    @Override
    public void lazyInit() {
        widgetViewDelegate = createWidgetViewDelegate();
        widgetViewDelegate.onCreate();
    }

    @Override
    public void onCreate() {
        //empty
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        widgetViewDelegate.onDestroy();
    }

    @Override
    public WidgetViewPersistentScope getPersistentScope() {
        return widgetViewDelegate.getPersistentScope();
    }

    /**
     * Вручную вызывает onCompletelyDestroy у виджета
     */
    public void manualCompletelyDestroy() {
        widgetViewDelegate.onCompletelyDestroy();
    }

    private void initWidgetViewDelegate() {
        if (!isManualInitEnabled) {
            widgetViewDelegate = createWidgetViewDelegate();
        }
    }
}
