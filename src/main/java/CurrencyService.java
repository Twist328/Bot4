import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;

public class CurrencyService {

    private static final String API_URL = "https://www.cbr.ru/scripts/XML_daily.asp";

    private final Map<String, Double> rates = new ConcurrentHashMap<>();

    private final List<String> supportedCurrencies = List.of("USD", "EUR", "CNY", "KZT", "GBP", "JPY","CAD","AUD","NZD");

    public CurrencyService() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        long delay = computeInitialDelayInSeconds();
        scheduler.scheduleAtFixedRate(this::updateRates, delay, 86400, TimeUnit.SECONDS);

        updateRates(); // Первичная загрузка
    }

    private long computeInitialDelayInSeconds() {
        LocalDate now = LocalDate.now();
        LocalDate next = now.plusDays(1);
        return java.time.Duration.between(java.time.LocalDateTime.now(),
                java.time.LocalDateTime.of(next, java.time.LocalTime.MIDNIGHT)).getSeconds();
    }

    public String getRateFor(String currencyCode) {
        currencyCode = currencyCode.toUpperCase();
        //String currencyCode = input.replaceAll("[^A-Z]", "");
        if (!rates.containsKey(currencyCode)) {
            return "🤷‍♂️ Извините, я пока не умею показывать курс такой валюты.";
        }

        double rate = rates.get(currencyCode);
        return String.format("💱 Курс %s к RUB: %.4f", currencyCode, rate);
    }

    public void updateRates() {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new URL(API_URL).openStream());

            NodeList valuteList = doc.getElementsByTagName("Valute");
            Map<String, Double> updated = new HashMap<>();

            for (int i = 0; i < valuteList.getLength(); i++) {
                Element valute = (Element) valuteList.item(i);
                String charCode = valute.getElementsByTagName("CharCode").item(0).getTextContent();

                String valueStr = valute.getElementsByTagName("Value").item(0).getTextContent();
                String nominalStr = valute.getElementsByTagName("Nominal").item(0).getTextContent();

                double value = Double.parseDouble(valueStr.replace(',', '.'));
                int nominal = Integer.parseInt(nominalStr);
                double rate = value / nominal;

                updated.put(charCode, rate);
            }

            updated.put("RUB", 1.0);
            rates.clear();
            rates.putAll(updated);
            LocalDate lastUpdateDate = LocalDate.now();
            System.out.println("✅ Курсы обновлены: " + lastUpdateDate);

        } catch (Exception e) {
            System.err.println("❌ Ошибка обновления курсов: " + e.getMessage());
        }
    }

    public List<String> getSupportedCurrencies() {
        return supportedCurrencies;
    }

    public String getOtherCurrenciesList() {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new URL(API_URL).openStream());

            NodeList valuteList = doc.getElementsByTagName("Valute");

            StringBuilder result = new StringBuilder("📋 Другие доступные валюты:\n\n");

            for (int i = 0; i < valuteList.getLength(); i++) {
                Element valute = (Element) valuteList.item(i);
                String charCode = valute.getElementsByTagName("CharCode").item(0).getTextContent();

                if (supportedCurrencies.contains(charCode)) continue;

                String name = valute.getElementsByTagName("Name").item(0).getTextContent();
                String valueStr = valute.getElementsByTagName("Value").item(0).getTextContent();
                String nominalStr = valute.getElementsByTagName("Nominal").item(0).getTextContent();

                double value = Double.parseDouble(valueStr.replace(',', '.'));
                int nominal = Integer.parseInt(nominalStr);
                double rate = value / nominal;

                result.append(String.format("💸 %s (%s) — %.4f RUB\n", charCode, name, rate));
            }

            result.append("\n✍️ Просто отправь код валюты, чтобы узнать её курс.");
            return result.toString();

        } catch (Exception e) {
            return "😔 Не удалось загрузить список валют.";
        }
    }
}
