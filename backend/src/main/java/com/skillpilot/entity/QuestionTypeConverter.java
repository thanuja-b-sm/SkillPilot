package com.skillpilot.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class QuestionTypeConverter implements AttributeConverter<QuestionType, String> {

    @Override
    public String convertToDatabaseColumn(QuestionType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name().toLowerCase();
    }

    @Override
    public QuestionType convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return QuestionType.SINGLE;
        }
        for (QuestionType qt : QuestionType.values()) {
            if (qt.name().equalsIgnoreCase(dbData)) {
                return qt;
            }
        }
        return QuestionType.SINGLE;
    }
}
