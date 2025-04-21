package com.example;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CurrencyBot extends TelegramLongPollingBot {

    private final CurrencyService currencyService = new CurrencyService();

    @Override
    public String getBotUsername() {
        return "CrazywetherBot"; // Заменить на имя бота
    }

    @Override
    public String getBotToken() {
        return EnvLoader.get("TELEGRAM_BOT_TOKEN");
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String messageText = update.getMessage().getText().trim().toUpperCase();

            SendMessage message;

            switch (messageText) {
                case "/START":
                case "🚀 СТАРТ":
                    message = new SendMessage(chatId, "👋 Привет! Выбери валюту из кнопок или введи её код.");
                    message.setReplyMarkup(buildKeyboard());
                    break;

                case "/LIST":
                case "📋 СПИСОК ВАЛЮТ":
                    message = new SendMessage(chatId, currencyService.getOtherCurrenciesList());
                    message.setParseMode("Markdown");
                    message.setReplyMarkup(buildKeyboard());
                    break;

                case "🇺🇸 USD":
                case "🇪🇺 EUR":
                case "🇨🇳 CNY":
                case "🇰🇿 KZT":
                case "🇬🇧 GBP":
                case "🇯🇵 JPY":
                case "🇨🇦 CAD":
                case "🇦🇺 AUD":
                case "🇳🇿 NZD":
                    message = new SendMessage(chatId, currencyService.getRateFor(messageText.replaceAll("[^A-Z]", "")));
                    message.setReplyMarkup(buildKeyboard());
                    break;

                default:
                    message = new SendMessage(chatId, currencyService.getRateFor(messageText));
                    message.setReplyMarkup(buildKeyboard());
                    break;
            }

            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private ReplyKeyboardMarkup buildKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> rows = new ArrayList<>();

        // Первая строка
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("🇺🇸 USD"));
        row1.add(new KeyboardButton("🇪🇺 EUR"));
        row1.add(new KeyboardButton("🇨🇳 CNY"));

        // Вторая строка
        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("🇰🇿 KZT"));
        row2.add(new KeyboardButton("🇬🇧 GBP"));
        row2.add(new KeyboardButton("🇯🇵 JPY"));

        // Третья строка
        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton("🚀 СТАРТ"));
        row3.add(new KeyboardButton("📋 СПИСОК ВАЛЮТ"));

        // Четвёртая строка
        KeyboardRow row4 = new KeyboardRow();
        row4.add(new KeyboardButton("🇨🇦 CAD"));
        row4.add(new KeyboardButton("🇦🇺 AUD"));
        row4.add(new KeyboardButton("🇳🇿 NZD"));

        rows.add(row1);
        rows.add(row2);
        rows.add(row3);
        rows.add(row4);

        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }
}