package ru.phystech.java2.structured;

import java.io.Closeable;

/**
 * Расширенный интерфейс {@link TableProvider}, с предоставлением метода {@link #close()}.
 */
public interface RemoteTableProvider extends TableProvider, Closeable {
}
