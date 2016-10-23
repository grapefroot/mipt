package ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap;

public final class MultiFileHashMapMain {
    public static void main(final String[] args) throws Exception {
        if (args.length == 0) {
            MultiFileHashMapModes.interactive();
        } else {
            MultiFileHashMapModes.batch(args);
        }
    }
}
