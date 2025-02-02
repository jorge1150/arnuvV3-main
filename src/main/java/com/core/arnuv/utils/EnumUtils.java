package com.core.arnuv.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EnumUtils {

    // Método genérico para obtener los valores de cualquier Enum
    public static <E extends Enum<E>> List<EnumOption> getEnumOptions(Class<E> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(e -> new EnumOption(e.name(), e.toString()))
                .collect(Collectors.toList());
    }

    // Clase interna para representar las opciones del ComboBox
    public static class EnumOption {
        private String value;
        private String label;

        public EnumOption(String value, String label) {
            this.value = value;
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public String getLabel() {
            return label;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
