package com.liner.ragebot.game;

public enum BotState {
    SELECTING_INTERFACE("Выбор интерфейса"),
    SELECTING_WINDOW("Выбор окна игры"),
    INITIALIZATION_FAIL("Ошибка инициализации"),
    INITIALIZATION("Инициализация"),
    CAPTCHA_START("Поиск решения каптчи"),
    CAPTCHA_FINISH("Каптча разгадана"),
    CAPTCHA_FAIL("Каптча провалена"),
    INVENTORY_CHECK("Проверка инвентаря"),
    PASSIVE_MODE_ON("Включение пасивноного режима"),
    PASSIVE_MODE_OFF("Выключение пасивноного режима"),
    PASSIVE_MODE_WAIT("Ожидание 30 сек"),
    INVENTORY_OPEN("Открытые ивентаря"),
    INVENTORY_CLOSE("Закрытие инвентаря"),
    INVENTORY_RODS_CHECK("Проверка удочек"),
    INVENTORY_BAITS_CHECK("Проверка наживок"),
    SUICIDE("Суицид"),
    STOPPED("Остановлено"),
    STARTED("Запущено"),
    SELECTING_BAIT("Выбор наживки"),
    THROW_ROD("Бросок удочки"),
    WAITING("Ожидание"),
    WAITING_FISH("Ожидание клёва"),
    PICKING_FISH("Вылавливание рыбы"),
    FISHING_FINISH("Рыба поймана"),
    NO_RODS("Нет доступных удочек"),
    NO_BAITS("Нет доступных наживок"),
    NO_SELECTED_BAIT("Нет выбранной наживки"),
    NO_SELECTED_ROD("Нет выбранной удочки"),
    FISHING_FAIL("Рыба сорвалась");

    private final String description;

    BotState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
