package ru.mivanov.counter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * ThreadSafe имплементация счетчика с максмальным значением.
 * <p>
 * <b>{@link IIncrementorImpl#maximumValue}</b> - максимальное значение
 * счетчика. Диапазон изменения от <b>0</b> до <b>{@link Integer#MAX_VALUE}</b>
 * (значение по-умолчанию). Устанавливается методом
 * {@link IIncrementorImpl#setMaximumValue}.
 * <p>
 * <b>{@link IIncrementorImpl#counter}</b> - текущее значение
 * счетчика. Отсчет начинается с нуля и увеличивается на единицу при
 * каждом вызове метода <b>{@link IIncrementorImpl#incrementNumber}</b>.
 * При вызове метода после достижения максимального значения,
 * производится обнуление счетчика и отчет начинается заново с 0.
 *
 *
 * @autor Макс Иванов
 * @version 1.0
 * Date:   2019/09/04
 */
public class IIncrementorImpl implements IIncrementor {
    /** Максимальное значение счетчика */
    private final AtomicInteger maximumValue = new AtomicInteger(Integer.MAX_VALUE);
    /** Текущее значение счетчика */
    private final AtomicInteger counter = new AtomicInteger(0);

    /**
     * @return текущее значение счетчика.
     */
    public int getNumber() {
        return counter.get();
    }

    /**
     * Увеличивает значение счетчика на 1 при каждом вызове.
     * При вызове метода после достижения максимального значения,
     * производится обнуление счетчика и отчет начинается заново с 0.
     */
    public void incrementNumber() {
        counter.updateAndGet(
                (n) -> n < maximumValue.get() ? n + 1 : 0);
    }

    /**
     * Устанавливает максимальное значение счетчика.
     * Если новое максимальное значение меньше текущего значения
     * счетчика, то счетчик обнуляется.
     * @param maximumValue максимальное значение счетчика.
     * @throws NegativeMaximumValueException в случае maximumValue < 0.
     */
    public void setMaximumValue(int maximumValue) {
        if (maximumValue >= 0) {
            this.maximumValue.updateAndGet(
                    (m) -> {
                        counter.updateAndGet((n) -> n > maximumValue ? 0 : n);
                        return maximumValue;
                    });
        } else {
            throw new NegativeMaximumValueException(
                    String.format(
                            "Недопустимое значение:%s. Максимальное значение не может быть меньше 0.",
                            maximumValue));
        }
    }
}
