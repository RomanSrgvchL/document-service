package com.group.itq.enums;

import lombok.Getter;

@Getter
public enum DocumentSortFields {
    ID("id"),
    USERNAME("name"),
    CREATED_AT("createdAt"),
    UPDATED_AT("updatedAt"),;

    private final String fieldName;

    DocumentSortFields(String fieldName) {
        this.fieldName = fieldName;
    }
}
