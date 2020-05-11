package ru.job4j.total;

import android.os.Environment;

import java.io.File;

class FilesRepo {
    private final static File DIRECTORY = Environment.getExternalStorageDirectory();
    final static String ABSOLUTE_PATH = DIRECTORY.getAbsolutePath();
    private File[] files;

    FilesRepo() {
        this.files = DIRECTORY.listFiles();
    }

    File[] getFiles() {
        return files;
    }
}
