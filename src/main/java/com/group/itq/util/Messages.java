package com.group.itq.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Messages {

    public static final String PAGE_POSITIVE_OR_ZERO = "Число страниц должно быть >= 0";
    public static final String SIZE_POSITIVE = "Размер страницы должен быть > 0";

    public static final String ID_POSITIVE = "ID должен быть > 0";

    public static final String ID_REQUIRED = "ID обязателен";
    public static final String ID_LIST_INVALID = "Список ID должен содержать от 1 до 1000 элементов";

    public static final String AUTHOR_REQUIRED = "Автор обязателен";
    public static final String AUTHOR_MAX_LENGTH = "Имя автора не должно превышать 100 символов";

    public static final String NAME_REQUIRED = "Название документа обязательно";
    public static final String NAME_MAX_LENGTH = "Название не должно превышать 200 символов";

    public static final String DOCUMENT_NUMBER_MAX_LENGTH = "Номер документа не должен превышать 36 символов";

    public static final String INITIATOR_REQUIRED = "Инициатор обязателен";
    public static final String INITIATOR_MAX_LENGTH = "Имя инициатора не должно превышать 30 символов";

    public static final String APPROVER_REQUIRED = "Утверждающий обязателен";
    public static final String APPROVER_MAX_LENGTH = "Имя утверждающего не должно превышать 30 символов";

    public static final String COMMENT_MAX_LENGTH = "Комментарий не должен превышать 500 символов";

    public static final String STATUS_REQUIRED = "Статус обязателен";

    public static final String THREADS_MIN = "Количество потоков должно быть не меньше 1";
    public static final String THREADS_MAX = "Количество потоков должно быть не больше 5";
    public static final String ATTEMPTS_MIN = "Количество попыток должно быть не меньше 1";
    public static final String ATTEMPTS_MAX = "Количество попыток должно быть не больше 100";
}
