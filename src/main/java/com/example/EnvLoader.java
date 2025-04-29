package com.example;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class EnvLoader {
    private static final Properties props = new Properties();

    static {
        try {
            props.load(new FileInputStream(".env"));
            System.out.println("✅ Загружен .env файл локально.");
        } catch (IOException e) {
            System.out.println("ℹ️ .env файл не найден, использую переменные окружения...");
        }
    }

    public static String get(String key) {
        String value = props.getProperty(key);
        if (value == null) {
            value = System.getenv(key);
        }
        return value;
    }
}
