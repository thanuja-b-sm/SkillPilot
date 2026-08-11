package com.skillpilot.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class DemandLevelConverter implements AttributeConverter<DemandLevel, String> {

    @Override
    public String convertToDatabaseColumn(DemandLevel attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getValue();
    }

    @Override
    public DemandLevel convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return DemandLevel.HIGH;
        }
        for (DemandLevel dl : DemandLevel.values()) {
            if (dl.getValue().equalsIgnoreCase(dbData) || dl.name().equalsIgnoreCase(dbData)) {
                return dl;
            }
        }
        return DemandLevel.HIGH;
    }
}
