package ru.practicum.ewm.utils;

public class ExceptionMessages {

    // NotFoundException
    public static final String USER_NO_ID = "Пользователь с таким id не существует.";
    public static final String CATEGORY_NO_ID = "Категории с таким id не существует.";
    public static final String EVENT_NO_ID = "События с таким id не существует или оно не доступно.";
    public static final String COMPILATION_NO_ID = "Подборки событий с таким id не существует.";
    public static final String REQUEST_NO_ID = "Запроса на участие в мероприятие с таким id не существует.";
    public static final String REQUEST_WRONG_LIST = "Некоторые id запросов на участие из списка не существуют.";
    public static final String COMPILATION_WRONG_EVENT_LIST = "Некоторые id событий из списка не существуют.";

    // bad request exceptions
    public static final String EVENT_WRONG_INITIATOR = "Данный пользователь не является инициатором события.";
    public static final String WRONG_REQUESTER = "Данный пользователь не является автором запроса.";
    public static final String COMPILATION_WRONG_TITLE = "Подборка с таким заголовком уже существует.";
    public static final String WRONG_EMAIL = "Пользователь с таким email уже существует.";
    public static final String START_DT_AFTER_RND_DT = "Конец и начало дат поиска заданы неверно";
    public static final String EVENT_DATE_IN_PAST = "Нельзя задать дату события в прошлом";

    // DataConflictException
    public static final String CATEGORY_WRONG_NAME = "Категория с таким названием уже существует.";
    public static final String USER_WRONG_NAME = "Пользователь с таким именем уже существует.";
    public static final String CATEGORY_CONSIST_EVENTS = "Нельзя удалить категорию, содержащую события";
    public static final String WRONG_PUBLISHING = "Нельзя публиковать уже опубликованное или отменённое событие";
    public static final String WRONG_REJECTING = "Нельзя отклонить уже опубликованное событие";
    public static final String REQUESTER_EXISTS = "Данный пользователь уже отправил запрос для участия в этом событии.";
    public static final String REQUESTER_IS_INITIATOR = "Пользователь отправляет запрос на собственное мероприятие.";
    public static final String REQUEST_UNPUBLISHED = "Нельзя добавить запрос на участи в неопубликованном событии";
    public static final String CONFIRMED_REQUEST_LIMITS = "Достигнуто предельное количество участников мероприятия.";
    public static final String REJECT_CONFIRMED_REQUEST = "Нельзя отменить уже одобренный запрос";
    public static final String EDIT_PUBLISHED_EVENT = "Нельзя изменять опубликованное событие";
}
