import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class CurrencyBot extends TelegramLongPollingBot {

    private final CurrencyService currencyService = new CurrencyService();

    @Override
    public String getBotUsername() {
        return "YourBotUsername"; // замените на имя
    }

    @Override
    public String getBotToken() {
        return "1675589814:AAFoAl8VGq5nFxSX0_OfP584DJkn43i5nn8"; // замените на токен
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
                    message = new SendMessage(chatId, currencyService.getRateFor("USD"));
                    message.setReplyMarkup(buildKeyboard());
                    break;

                case "🇪🇺 EUR":
                    message = new SendMessage(chatId, currencyService.getRateFor("EUR"));
                    message.setReplyMarkup(buildKeyboard());
                    break;

                case "🇨🇳 CNY":
                    message = new SendMessage(chatId, currencyService.getRateFor("CNY"));
                    message.setReplyMarkup(buildKeyboard());
                    break;

                case "🇰🇿 KZT":
                    message = new SendMessage(chatId, currencyService.getRateFor("KZT"));
                    message.setReplyMarkup(buildKeyboard());
                    break;

                case "🇬🇧 GBP":
                    message = new SendMessage(chatId, currencyService.getRateFor("GBP"));
                    message.setReplyMarkup(buildKeyboard());
                    break;

                case "🇯🇵 JPY":
                    message = new SendMessage(chatId, currencyService.getRateFor("JPY"));
                    message.setReplyMarkup(buildKeyboard());
                    break;

                case "🇨🇦 CAD":
                    message = new SendMessage(chatId, currencyService.getRateFor("CAD"));
                    message.setReplyMarkup(buildKeyboard());
                    break;

                case "🇦🇺 AUD":
                    message = new SendMessage(chatId, currencyService.getRateFor("AUD"));
                    message.setReplyMarkup(buildKeyboard());
                    break;

                case "🇳🇿 NZD":
                    message = new SendMessage(chatId, currencyService.getRateFor("NZD"));
                    message.setReplyMarkup(buildKeyboard());
                    break;

                default:
                    // Пробуем получить курс по введённому вручную коду
                    String rate = currencyService.getRateFor(messageText);
                    message = new SendMessage(chatId, rate);
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

        // Первая строка — основные валюты
        KeyboardRow row1 = new KeyboardRow();
        row1.add("🇺🇸 USD");
        row1.add("🇪🇺 EUR");
        row1.add("🇨🇳 CNY");

        // Вторая строка
        KeyboardRow row2 = new KeyboardRow();
        row2.add("🇰🇿 KZT");
        row2.add("🇬🇧 GBP");
        row2.add("🇯🇵 JPY");

        // Третья строка — команды
        KeyboardRow row3 = new KeyboardRow();
        row3.add("🚀 СТАРТ");
        row3.add("📋 СПИСОК ВАЛЮТ");
        KeyboardRow row4 = new KeyboardRow();

        row4.add("🇨🇦 CAD");
        row4.add("🇦🇺 AUD");
        row4.add("🇳🇿 NZD");

        rows.add(row1);
        rows.add(row2);
        rows.add(row3);
        rows.add(row4);

        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }
}
