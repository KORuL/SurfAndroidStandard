[Главная](../main.md)

[TOC]

# Асинхронные взаимодействия

Для подавляющего количества асинхронных задач следует использовать **RxJava/RxKotlin**.

Использование Rx, например в Presenter(при построении MVP архитектуры),
помогает [решить проблемы смены конфигурации](../../core-mvp/README.md).

Поток, в котором будет выполняться асинхронная задача следует указывать
непосредственно перед подпиской на Observable.
Это необходимо для выполнения составных асинхронных задач в одном
потоке.

Для указания потока выполнения и потока подписки асинхронной
задачи, обернутой в Observable присутствует класс [SchedulersProvider](../../rx-extension/src/main/java/ru/surfstudio/android/rx/extension/scheduler/SchedulersProviderImpl.java)
из модуля [rx-extension](../../rx-extension/README.md),
предоставляющий доступ к Scheduler главного и рабочего потока.
**`Использование классов rx.Schedulers и AndroidSchedulers запрещено.`**
Эта абстракция была создана для возможности тестирования асинхронного
взаимодействия между модулями.

Также в *rx-extension* представлены полезные интерфейсы и утилиты для работы с Rx.

В студийной практике принято оборачивать те или иные события в rx.
Например, есть готовые обертки для:
- [BroadcastReceiver](../../broadcast-extension/README.md)
- Состояния соединения,[также на Broadcast](../../connection/README.md)
- Событие [onActivityResult](../../core-ui/README.md)
- [RuntimePermissions](../../core-ui/README.md)
- [Работа с сетью](../../network/README.md)
- [Локация](../location/README.md)
- [Получение фотографий из галлереи](../../picture-provider/README.md)