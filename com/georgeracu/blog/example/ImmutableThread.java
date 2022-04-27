package com.georgeracu.blog.example;

public final class ImmutableThread {
    public static void main(String args[]) {
        MutabilityTest.shouldAddNumbers();
    }

    private static void runMutableMethod() {
        System.out.println("I am running in mutable method");
        // MutableObject mutableObject = new MutableObject(0);
        // Runnable runnable = new RunnableObject(mutableObject);
        // System.out.println("Value of mutable object is: " + mutableObject.getValue()
        // + " and threadId: " + Thread
        // .currentThread().getId());
        // new Thread(runnable).start();
        // mutableObject.addValue(-5000);
    }
}

class RunnableObject implements Runnable {
    private final MutableObject mutableObject;
    private final int iterations;

    public RunnableObject(final MutableObject mutableObject, final int iterations) {
        this.mutableObject = mutableObject;
        this.iterations = iterations;
    }

    @Override
    public void run() {
        for (int i = 0; i < iterations; i++) {
            mutableObject.addValue(1);
            System.out.println("Value of mutable object is: " + mutableObject.getValue() + " and threadId: " + Thread
                    .currentThread().getId());
            try {
                System.out.println("Simulating a 5ms delay in execution");
                Thread.sleep(5);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public MutableObject getMutableObject() {
        return this.mutableObject;
    }
}

final class MutableObject {
    private int value;

    public MutableObject(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void addValue(int newValue) {
        this.value += newValue;
    }
}

final class ImmutableObject {
    private final int value;

    public ImmutableObject(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public ImmutableObject addValue(int newValue) {
        int finalValue = this.value + newValue;
        return new ImmutableObject(finalValue);
    }
}

final class MutabilityTest {
    public static void shouldAddNumbers() {
        final MutableObject mutableObject = new MutableObject(0);
        final RunnableObject runnable = new RunnableObject(mutableObject, 10);
        System.out.println("Value of mutable object is: " + mutableObject.getValue() + " and threadId: " + Thread
                .currentThread().getId());
        new Thread(runnable).start();
        // mutableObject.addValue(-5000);
        final int expectedValue = 10;
        final int actualValue = runnable.getMutableObject().getValue();
        if (expectedValue != actualValue) {
            throw new RuntimeException(
                    "Expected value " + expectedValue + " is different from the actual value " + actualValue);
        }
    }
}