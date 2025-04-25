package com.example;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

class ForexService {

    private static final String API_KEY = EnvLoader.get("TWELVE_API_KEY");

    public String getRate(String pairCode) {
        if (API_KEY == null || API_KEY.isBlank()) {
            return "⚠️ API ключ не задан. Проверь .env (TWELVE_API_KEY)";
        }

        try {
            String symbol = pairCode.toUpperCase();

            // ➡️ Вставляем слэш между валютами (например, EUR/USD)
            if (!symbol.contains("/")) {
                symbol = symbol.substring(0, 3) + "/" + symbol.substring(3);
            }

            // ➡️ Кодируем слэш для URL
            String encodedSymbol = symbol.replace("/", "%2F");

            String urlString = "https://api.twelvedata.com/price?symbol=" + encodedSymbol + "&apikey=" + API_KEY;

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            Scanner scanner = new Scanner(conn.getInputStream());
            StringBuilder response = new StringBuilder();

            while (scanner.hasNextLine()) {
                response.append(scanner.nextLine());
            }

            scanner.close();
            conn.disconnect();

            // Логирование для отладки
            System.out.println(">> URL: " + urlString);
            System.out.println(">> API ответ: " + response);

            String price = extractPrice(response.toString());
            return price != null
                    ? "📈 " + symbol + ": " + price
                    : "❌ Не удалось получить курс по паре: " + symbol;

        } catch (IOException e) {
            return "❌ Ошибка при подключении к API: " + e.getMessage();
        }
    }

    private static String extractPrice(String json) {
        if (json.contains("\"status\":\"error\"")) {
            // Ошибка от API
            return null;
        }

        int priceIndex = json.indexOf("\"price\":\"");
        if (priceIndex == -1) return null;

        int start = priceIndex + 9;
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
}
/**
 * @program: Botss44
 * @description:
 * @autor: twist328
 * @create: 2025-04-24 12
 **/