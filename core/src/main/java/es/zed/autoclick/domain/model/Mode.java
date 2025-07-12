package es.zed.autoclick.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Mode {
    KEY, MOUSE, MIX;

    @JsonCreator
    public static Mode fromString(String value) {
        if (value == null) return null;
        try {
            return Mode.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid mode: " + value);
        }
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
