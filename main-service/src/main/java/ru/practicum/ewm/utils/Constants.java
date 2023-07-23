package ru.practicum.ewm.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Constants {
    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String STANDARD_TIME_REGEX = "^\\d{4}-((0[1-9])|(1[012]))-((0[1-9]|[12]\\d)|3[01]) ([0-1]\\d|2[0-3])(:[0-5]\\d){2}$";
    public static final String PAGINATION_FROM = "0";
    public static final String PAGINATION_SIZE = "10";
}