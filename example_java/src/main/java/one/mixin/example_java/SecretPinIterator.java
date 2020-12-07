package one.mixin.example_java;

import one.mixin.bot.PinIterator;

class SecretPinIterator implements PinIterator {
    @Override
    public long getValue() {
        return System.currentTimeMillis();
    }

    @Override
    public void increment() {

    }
}