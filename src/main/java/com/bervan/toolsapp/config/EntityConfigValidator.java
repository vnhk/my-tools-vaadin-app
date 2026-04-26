package com.bervan.toolsapp.config;

import com.bervan.common.config.BervanViewConfig;
import com.bervan.common.config.ClassViewAutoConfigColumn;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class EntityConfigValidator {

    private final BervanViewConfig viewConfig;

    public EntityConfigValidator(BervanViewConfig viewConfig) {
        this.viewConfig = viewConfig;
    }

    public record FieldError(String field, String message) {}

    public List<FieldError> validate(String entityName, Map<String, Object> fields) {
        List<FieldError> errors = new ArrayList<>();
        Map<String, ClassViewAutoConfigColumn> config = viewConfig.get(entityName);
        if (config == null) return errors;

        for (Map.Entry<String, ClassViewAutoConfigColumn> entry : config.entrySet()) {
            String fieldName = entry.getKey();
            ClassViewAutoConfigColumn col = entry.getValue();
            if (!fields.containsKey(fieldName)) continue;

            Object raw = fields.get(fieldName);
            String value = raw != null ? raw.toString() : null;

            if (col.isRequired() && (value == null || value.isBlank())) {
                errors.add(new FieldError(fieldName, col.getDisplayName() + " is required"));
                continue;
            }

            if (value != null && !value.isBlank()) {
                int len = value.length();
                if (len < col.getMin()) {
                    errors.add(new FieldError(fieldName,
                            col.getDisplayName() + " must be at least " + col.getMin() + " characters"));
                } else if (len > col.getMax()) {
                    errors.add(new FieldError(fieldName,
                            col.getDisplayName() + " must be at most " + col.getMax() + " characters"));
                }
            }
        }

        return errors;
    }
}
