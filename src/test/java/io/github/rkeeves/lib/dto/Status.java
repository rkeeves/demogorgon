package io.github.rkeeves.lib.dto;

public enum Status {
    NOT_ASKED("NOT_ASKED"),
    REJECTED("REJECTED"),
    FILTERED("FILTERED"),
    COMPLETED("COMPLETED");

    public final String name;

    private Status(String name) {
        this.name = name;
    }
}
