package com.example.carebridge.dto;

import java.util.List;

public class ClassificationResponse {
    private List<String> categories;

    public ClassificationResponse(List<String> categories) {
        this.categories = categories;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
}