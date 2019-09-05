package ru.mivanov.counter;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;

public class IIncrementorImplTest {

    @Test
    public void incrementAndGetToMaxValueTest() {
        IIncrementor iIncrementor = new IIncrementorImpl();
        assertEquals(iIncrementor.getNumber(), 0);
        iIncrementor.incrementNumber();
        assertEquals(iIncrementor.getNumber(), 1);
        for (int i = 0; i < Integer.MAX_VALUE - 1; i++) {
            iIncrementor.incrementNumber();
        }
        assertEquals(iIncrementor.getNumber(), Integer.MAX_VALUE);
        iIncrementor.incrementNumber();
        assertEquals(iIncrementor.getNumber(), 0);
        iIncrementor.incrementNumber();
        assertEquals(iIncrementor.getNumber(), 1);
    }

    @Test
    public void setNewZeroMaxValueAndIncrementAndGetTest() {
        IIncrementor iIncrementor = new IIncrementorImpl();
        assertEquals(iIncrementor.getNumber(), 0);
        iIncrementor.setMaximumValue(0);
        iIncrementor.incrementNumber();
        assertEquals(iIncrementor.getNumber(), 0);
        iIncrementor.incrementNumber();
        assertEquals(iIncrementor.getNumber(), 0);
    }

    @Test
    public void setNewRandomMaxValueAndIncrementAndGetTest() {
        IIncrementor iIncrementor = new IIncrementorImpl();
        int newMaxValue = new Random().nextInt(Integer.MAX_VALUE);
        iIncrementor.setMaximumValue(newMaxValue);
        for (int i = 0; i < newMaxValue; i++) {
            iIncrementor.incrementNumber();
        }
        assertEquals(iIncrementor.getNumber(), newMaxValue);
        iIncrementor.incrementNumber();
        assertEquals(iIncrementor.getNumber(), 0);
        iIncrementor.incrementNumber();
        assertEquals(iIncrementor.getNumber(), 1);
    }

    @Test
    public void setNewMaxValueBiggerCurrentValueTest() {
        IIncrementor iIncrementor = new IIncrementorImpl();
        int incrementNumbers = new Random().nextInt(10) + 5;
        for (int i = 0; i < incrementNumbers; i++) {
            iIncrementor.incrementNumber();
        }
        assertEquals(iIncrementor.getNumber(), incrementNumbers);
        iIncrementor.setMaximumValue(incrementNumbers);
        assertEquals(iIncrementor.getNumber(), incrementNumbers);
        iIncrementor.setMaximumValue(incrementNumbers - 1);
        assertEquals(iIncrementor.getNumber(), 0);
        iIncrementor.incrementNumber();
        assertEquals(iIncrementor.getNumber(), 1);
    }

    @Test(expected = NegativeMaximumValueException.class)
    public void setNegativeNewMaxValueTest() {
        IIncrementor iIncrementor = new IIncrementorImpl();
        iIncrementor.setMaximumValue(-1);
    }

    @Test
    public void multiThreadIncrementTest() {
        IIncrementor iIncrementor = new IIncrementorImpl();
        int maxNumber = new Random().nextInt(10) + 10;
        iIncrementor.setMaximumValue(maxNumber);
        int threadNumber = new Random().nextInt(1000) + 1000;
        int incrementNumber = new Random().nextInt(1000) + 1000;
        ExecutorService executor = Executors.newFixedThreadPool(50);
        List<Callable<Integer>> callables = new LinkedList<>();
        for (int i = 0; i < threadNumber; i++) {
            callables.add(() -> {
                for (int j = 0; j < incrementNumber; j++) {
                    iIncrementor.incrementNumber();
                }
                return 0;
            });
        }
        try {
            executor.invokeAll(callables)
                    .forEach(future -> {
                        try {
                            future.get();
                        } catch (Exception e) {
                            throw new IllegalStateException(e);
                        }
                    });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int totalIncrementNumber = threadNumber * incrementNumber;
        while (totalIncrementNumber > maxNumber) {
            totalIncrementNumber = totalIncrementNumber - maxNumber - 1;
        }
        assertEquals(iIncrementor.getNumber(), totalIncrementNumber);
    }

    @Test
    public void multiThreadIncrementGetAndSetMaxValueTest() {
        IIncrementor iIncrementor = new IIncrementorImpl();
        iIncrementor.setMaximumValue(new Random().nextInt(10) + 10);
        int threadNumber = new Random().nextInt(100) + 100;
        int incrementNumber = new Random().nextInt(1000) + 1000;
        ExecutorService executor = Executors.newFixedThreadPool(50);
        List<Callable<Integer>> callables = new LinkedList<>();
        for (int i = 0; i < threadNumber; i++) {
            callables.add(() -> {
                for (int j = 0; j < incrementNumber; j++) {
                    iIncrementor.incrementNumber();
                    iIncrementor.setMaximumValue(new Random().nextInt(10));
                    iIncrementor.getNumber();
                }
                return 0;
            });
        }
        try {
            executor.invokeAll(callables)
                    .forEach(future -> {
                        try {
                            future.get();
                        } catch (Exception e) {
                            throw new IllegalStateException(e);
                        }
                    });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}