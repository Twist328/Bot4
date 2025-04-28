package com.example;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.w3c.dom.*;

public class CurrencyService {

    private static final String CBR_URL = "https://www.cbr.ru/scripts/XML_daily.asp";
    private final Map<String, String> rates = new HashMap<>();

    public CurrencyService() {
        loadRates();
        startAutoReload();
    }
    public String getFormattedCurrencyList() {
        StringBuilder sb = new StringBuilder("*Курсы валют ЦБ РФ:*\n\n");

        int count = 0;
        for (String key : rates.keySet()) {
            String flag = getFlagForCurrency(key);
            sb.append(flag).append(" *").append(key).append("*").append("   ");
            count++;
            if (count % 3 == 0) sb.append("\n"); // каждые 3 валюты — новая строка
        }

        return sb.toString();
    }
    private void loadRates() {
        try {
            InputStream stream = new URL(CBR_URL).openStream();
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("Valute");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String charCode = element.getElementsByTagName("CharCode").item(0).getTextContent();
                    String value = element.getElementsByTagName("Value").item(0).getTextContent().replace(",", ".");
                    String nominal = element.getElementsByTagName("Nominal").item(0).getTextContent();
                    rates.put(charCode, formatRate(charCode, value, nominal));
                }
            }

            System.out.println("✅ Курсы ЦБ РФ обновлены.");

        } catch (Exception e) {
            System.err.println("❌ Ошибка загрузки курсов ЦБ РФ: " + e.getMessage());
        }
    }

    private void startAutoReload() {
        Timer timer = new Timer(true); // daemon thread
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println("⏰ Автоматическое обновление курсов...");
                loadRates();
            }
        }, 3600_000, 3600_000); // 1 час = 3600_000 мс
    }

    private String formatRate(String code, String value, String nominal) {
        String flag = getFlagForCurrency(code);
        return String.format("%s %s *%s* стоит *%s ₽*", flag, nominal, code, value);
    }

    private String getFlagForCurrency(String code) {
        return switch (code) {
            case "USD" -> "🇺🇸";
            case "EUR" -> "🇪🇺";
            case "GBP" -> "🇬🇧";
            case "JPY" -> "🇯🇵";
            case "CNY" -> "🇨🇳";
            case "CHF" -> "🇨🇭";
            case "AUD" -> "🇦🇺";
            case "CAD" -> "🇨🇦";
            case "BYN" -> "🇧🇾";
            case "KZT" -> "🇰🇿";
            case "RSD" -> "🇷🇸";
            case "VND" -> "🇻🇳";
            case "AMD" -> "🇦🇲";
            case "GEL" -> "🇬🇪";
            case "AED" -> "🇦🇪";
            case "NZD" -> "🇳🇿";
            case "PLN" -> "🇵🇱";
            case "ZAR" -> "🇵🇱";
            case "TRY" -> "🇹🇷";
            case "HUF" -> "🇭🇺";
            case "HKD" -> "🇭🇰";
            case "NOK" -> "🇳🇴";
            case "RON" -> "🇷🇴";
            case "CZK" -> "🇨🇿";
            case "UAH" -> "🇺🇦";
            case "QAR" -> "🇶🇦";
            case "INR" -> "🇮🇳";
            case "THB" -> "🇹🇭";
            case "KRW" -> "🇰🇷";
            case "IDR" -> "🇮🇩";
            case "DKK" -> "🇩🇰";
            case "XDR" ->"🌐IMF";
            case "SGD" -> "🇸🇬";
            case "KGS" -> "🇰🇬";
            case "TMT" -> "🇹🇲";
            case "UZS" -> "🇺🇿";
            case "MDL" -> "🇲🇩";
            case "TJS" -> "🇹🇯";
            case "BGN" -> "🇧🇬";
            case "EGP" -> "🇪🇬";
            case "AZN" -> "🇦🇿";
            case "SEK" -> "🇸🇪";
            case "BRL" -> "🇧🇷";
            default -> "💱";
        };
    }

    public String getRateFor(String code) {
        String upperCode = code.toUpperCase();
        return rates.getOrDefault(upperCode, "❌ Извините, не удалось найти курс для: " + upperCode);
    }

    public String getOtherCurrenciesList() {
        StringBuilder sb = new StringBuilder("*Курсы ЦБ РФ:*\n");
        for (String key : rates.keySet()) {
            sb.append("- ").append(key).append("\\n");
        }
        return sb.toString();
    }
}