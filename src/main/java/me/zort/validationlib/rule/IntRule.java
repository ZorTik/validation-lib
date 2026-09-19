package me.zort.validationlib.rule;

public class IntRule extends IntegerRule {
    private static final IntRule INSTANCE = new IntRule();

    public static IntRule getInstance() {
        return INSTANCE;
    }
}
