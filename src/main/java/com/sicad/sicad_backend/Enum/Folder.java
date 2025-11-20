package com.sicad.sicad_backend.Enum;

public enum Folder {
    PERFIL("profile"),
    DOC("documents");

    private final String folderName;

    Folder(String folderName) {
        this.folderName = folderName;
    }

    public String getName() {
        return folderName;
    }
}
