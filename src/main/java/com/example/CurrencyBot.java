package com.example;

import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class CurrencyBot extends TelegramLongPollingBot {

    private final CurrencyService currencyService = new CurrencyService();
    private final ForexService forexService = new ForexService();


    @Override
    public String getBotUsername() {
        return "CrazywetherBot";
    }

    @Override
    public String getBotToken() {
        return EnvLoader.get("TELEGRAM_BOT_TOKEN");
    }
    @Override
    public void onRegister() {
        List<BotCommand> commands = List.of(
                new BotCommand("/start", "🚀 Запуск бота"),
                new BotCommand("/list", "📋 Список валют ЦБ"),
                new BotCommand("/eurusd", "Курс EUR/USD"),
                new BotCommand("/gbpusd", "Курс GBP/USD"),
                new BotCommand("/gbpjpy", "Курс GBP/JPY"),
                new BotCommand("/eurchf", "Курс EUR/CHF"),
                new BotCommand("/eurgbp", "Курс EUR/GBP"),
                new BotCommand("/usdjpy", "Курс USD/JPY"),
                new BotCommand("/gbpcad", "Курс GBP/CAD"),
                new BotCommand("/gbpchf", "Курс GBP/CHF"),
                new BotCommand("/cadchf", "Курс CAD/CHF"),
                new BotCommand("/audusd", "Курс AUD/USD"),
                new BotCommand("/nzdaud", "Курс NZD/AUD"),
                new BotCommand("/nzdjpy", "Курс NZD/JPY"),
                new BotCommand("/audjpy", "Курс AUD/JPY")
        );

        try {
            execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
            System.out.println("✅ Меню команд установлено!");
        } catch (TelegramApiException e) {
            System.err.println("❌ Ошибка установки меню команд:");
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String messageText = update.getMessage().getText().trim().toUpperCase();

            System.out.println(">> Введено: " + messageText);
            SendMessage message;

            switch (messageText) {
                case "/START":
                case "🚀 СТАРТ":
                    message = new SendMessage(chatId, "👋 Привет! Выбери валюту из кнопок или введи её код.");
                    message.setReplyMarkup(buildKeyboard());
                    break;

                case "/LIST":
                case "📋 СПИСОК ВАЛЮТ":
                    message = new SendMessage(chatId, currencyService.getFormattedCurrencyList());
                    message.setParseMode("Markdown");
                    message.setReplyMarkup(buildKeyboard());
                    break;

                case "/EURUSD":
                case "/GBPUSD":
                case "/GBPJPY":
                case "/EURCHF":
                case "/EURGBP":
                case "/USDJPY":
                case "/GBPCAD":
                case "/GBPCHF":
                case "/CADCHF":
                case "/AUDUSD":
                case "/NZDAUD":
                case "/NZDJPY":
                case "/AUDJPY":
                    message = new SendMessage(chatId, forexService.getRate(messageText.replace("/", "")));
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
                case "🇦🇲 AMD":
                case "🇹🇷 TRY":
                case "🇬🇪 GEL":
                case "🇷🇸 RSD":
                case "🇧🇾 BYN":
                case "🇻🇳 VND":
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

        rows.add(new KeyboardRow(List.of(
                new KeyboardButton("🇺🇸 USD"),
                new KeyboardButton("🇪🇺 EUR"),
                new KeyboardButton("🇨🇳 CNY")
        )));

        rows.add(new KeyboardRow(List.of(
                new KeyboardButton("🇰🇿 KZT"),
                new KeyboardButton("🇬🇧 GBP"),
                new KeyboardButton("🇯🇵 JPY")
        )));

        rows.add(new KeyboardRow(List.of(
                new KeyboardButton("🚀 СТАРТ"),
                new KeyboardButton("📋 СПИСОК ВАЛЮТ")
        )));

        rows.add(new KeyboardRow(List.of(
                new KeyboardButton("🇨🇦 CAD"),
                new KeyboardButton("🇦🇺 AUD"),
                new KeyboardButton("🇳🇿 NZD")
        )));

        rows.add(new KeyboardRow(List.of(
                new KeyboardButton("🇦🇲 AMD"),
                new KeyboardButton("🇹🇷 TRY"),
                new KeyboardButton("🇬🇪 GEL")
        )));

        rows.add(new KeyboardRow(List.of(
                new KeyboardButton("🇷🇸 RSD"),
                new KeyboardButton("🇧🇾 BYN"),
                new KeyboardButton("🇻🇳 VND")
        )));

        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }
}
