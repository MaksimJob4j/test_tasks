package ru.mivanov.counter;

/**
 * Исключение при задании отрицательного значения максимального значения счетчика.
 */
public class NegativeMaximumValueException extends RuntimeException {
    public NegativeMaximumValueException(String msg) {
        super(msg);
    }
}
