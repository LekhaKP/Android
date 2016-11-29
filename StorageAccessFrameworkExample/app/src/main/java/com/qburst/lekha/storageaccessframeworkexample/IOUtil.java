package com.qburst.lekha.storageaccessframeworkexample;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by user on 25/10/16.
 */

public class IOUtil {
    public static void forceClose(Closeable closeable) {
        if (closeable == null)
            return;
        try {
            closeable.close();
        } catch (IOException e) {
            // ignore
        }
    }
}
