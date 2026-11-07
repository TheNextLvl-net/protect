package net.thenextlvl.protect.flag;

record SimpleFlag<T>(FlagInstance<T> instance, T value) implements Flag<T> {
}
