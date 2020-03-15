package ru.job4j.total;


import java.util.Objects;

public class FileModel {
    private String name;
    private String absolutePath;
    private String parent;

    FileModel(String name, String absolutePath, String parent) {
        this.name = name;
        this.absolutePath = absolutePath;
        this.parent = parent;

    }

    String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FileModel fileModel = (FileModel) o;
        return Objects.equals(name, fileModel.name)
                && Objects.equals(absolutePath, fileModel.absolutePath)
                && Objects.equals(parent, fileModel.parent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, absolutePath, parent);
    }
}

