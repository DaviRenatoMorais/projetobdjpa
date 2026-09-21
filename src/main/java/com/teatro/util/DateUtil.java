package com.teatro.util;

import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor
public class DateUtil {

    public static final DateTimeFormatter FORMATO_BR =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static LocalDate converter(String texto) {

        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(
                    "A data é obrigatória."
            );
        }

        try {
            return LocalDate.parse(
                    texto.trim(),
                    FORMATO_BR
            );
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Data inválida. Use o formato DD/MM/AAAA."
            );
        }
    }

    public static String formatar(LocalDate data) {

        if (data == null) {
            return "";
        }

        return data.format(FORMATO_BR);
    }
}