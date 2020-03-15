package ru.job4j.total;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class FilesTree {
    private static FilesTree instance;


    private FilesTree() {
    }

    static FilesTree getInstance() {
        if (instance == null) {
            instance = new FilesTree();
        }
        return instance;
    }

    List<FileModel> filesList(String path) {
        List<FileModel> filesList = new ArrayList<>();
        File file = new File(path);
        File[] files = file.listFiles();

        for (File f : files) {
            filesList.add(new FileModel(f.getName(), f.getAbsolutePath(), f.getParent()));
        }
        return filesList;
    }
}
