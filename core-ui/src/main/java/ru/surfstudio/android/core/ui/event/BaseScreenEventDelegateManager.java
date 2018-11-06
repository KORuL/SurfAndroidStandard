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
package ru.surfstudio.android.core.ui.event;

import androidx.annotation.Nullable;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.surfstudio.android.core.ui.ScreenType;
import ru.surfstudio.android.core.ui.event.base.ScreenEvent;
import ru.surfstudio.android.core.ui.event.base.ScreenEventDelegate;
import ru.surfstudio.android.core.ui.event.base.resolver.ScreenEventResolver;

/**
 * базовый класс менеджера {@link ScreenEventDelegateManager}
 * занимается регистрацией делегатов и оповещением их о событиях экрана
 */
public class BaseScreenEventDelegateManager implements ScreenEventDelegateManager {

    //список делегатов, зарегистрированных в этом менеджере
    private Set<ScreenEventDelegate> delegates = new HashSet<>();
    //список делегатов, переданных на регистрацию в родительский менеджер
    private Set<ScreenEventDelegate> throughDelegates = new HashSet<>();
    //список всех поддерживаемых событий и их делегатов
    private List<ScreenEventResolver> eventResolvers;
    //родительский менеджер делегатов
    private ScreenEventDelegateManager parentDelegateManger;
    //тип эрана контейнера
    private ScreenType screenType;
    private boolean destroyed = false;

    public BaseScreenEventDelegateManager(List<ScreenEventResolver> eventResolvers,
                                          @Nullable ScreenEventDelegateManager parentDelegateManger,
                                          ScreenType screenType) {
        this.eventResolvers = eventResolvers;
        this.parentDelegateManger = parentDelegateManger;
        this.screenType = screenType;
    }

    @Override
    public void registerDelegate(ScreenEventDelegate delegate) {
        registerDelegate(delegate, null);
    }

    @Override
    public void registerDelegate(ScreenEventDelegate delegate, @Nullable ScreenType emitterType) {
        assertNotDestroyed();
        //находим все EventResolvers, посколку delegate может реализовывать сразу несколько интерфейсов делегатов
        List<ScreenEventResolver> supportedResolvers = getEventResolversForDelegate(delegate);
        if (supportedResolvers.isEmpty()) {
            throw new IllegalArgumentException(String.format("No EventResolver for this delegate %s",
                    delegate.getClass().getCanonicalName()));
        }
        for (ScreenEventResolver eventResolver : supportedResolvers) {
            if (eventResolver.getEventEmitterScreenTypes().contains(screenType)
                    && (emitterType == null || screenType == emitterType)) {
                delegates.add(delegate);
            } else {
                if (parentDelegateManger == null) {
                    throw new IllegalStateException(String.format("No BaseScreenEventDelegateManager for register delegate %s",
                            delegate.getClass().getCanonicalName()));
                }
                throughDelegates.add(delegate);
                parentDelegateManger.registerDelegate(delegate);
            }
        }
    }


    @Override
    public <E extends ScreenEvent, D extends ScreenEventDelegate, R> R sendEvent(E event) {
        assertNotDestroyed();
        ScreenEventResolver<E, D, R> eventResolver = getEventResolverForEvent(event);
        if (eventResolver == null) {
            throw new IllegalArgumentException(String.format("No EventResolver for this event %s",
                    event.getClass().getCanonicalName()));
        }
        if (!eventResolver.getEventEmitterScreenTypes().contains(screenType)) {
            throw new IllegalArgumentException(String.format("event %s cannot be emitted from %s",
                    event.getClass().getCanonicalName(), screenType));
        }
        Class<D> delegateType = eventResolver.getDelegateType();
        List<D> delegates = getDelegates(delegateType);
        return eventResolver.resolve(delegates, event);
    }

    @Override
    public boolean unregisterDelegate(ScreenEventDelegate delegate) {
        boolean removedFromCurrent = delegates.remove(delegate);
        boolean removedFromParent = throughDelegates.remove(delegate)
                && parentDelegateManger != null
                && parentDelegateManger.unregisterDelegate(delegate);
        return removedFromCurrent || removedFromParent;
    }

    @Override
    public void destroy() {
        destroyed = true;
        for (ScreenEventDelegate delegate : throughDelegates) {
            if (parentDelegateManger != null) {
                parentDelegateManger.unregisterDelegate(delegate);
            }
        }
        throughDelegates.clear();
        delegates.clear();
    }

    private <D extends ScreenEventDelegate> List<D> getDelegates(Class<? extends D> clazz) {
        return Stream.of(delegates)
                .filter(clazz::isInstance)
                .map(delegate -> (D) delegate)
                .collect(Collectors.toList());
    }

    private List<ScreenEventResolver> getEventResolversForDelegate(ScreenEventDelegate delegate) {
        List<ScreenEventResolver> resultResolvers = new ArrayList<>();
        for (ScreenEventResolver eventResolver : eventResolvers) {
            if (eventResolver.getDelegateType().isInstance(delegate)) {
                resultResolvers.add(eventResolver);
            }
        }
        return resultResolvers;
    }

    @Nullable
    private <E extends ScreenEvent, D extends ScreenEventDelegate, R> ScreenEventResolver<E, D, R> getEventResolverForEvent(E event) {
        for (ScreenEventResolver eventResolver : eventResolvers) {
            if (eventResolver.getEventType().isInstance(event)) {
                return eventResolver;
            }
        }
        return null;
    }

    private void assertNotDestroyed() {
        if (destroyed) {
            throw new IllegalStateException(String.format("Unsupported operation, EventDelegateManager %s is destroyed", this));
        }
    }
}