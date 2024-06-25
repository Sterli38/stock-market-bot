package com.example.stockmarketbot.config;

import lombok.Data;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@Data
public class LanguageSettings {
    public final MessageSource messageSource;
    private Locale locale = Locale.US;

    public String getLocalizedMessage(String key, Object[] args) {
        return messageSource.getMessage(key, args, locale);
    }
}
