package ru.mivanov.counter;

public interface IIncrementor {
    int getNumber();
    void incrementNumber();
    void setMaximumValue(int maximumValue);
}