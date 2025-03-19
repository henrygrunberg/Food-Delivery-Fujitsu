package com.fooddelivery.fooddeliveryfujitsu.service;

import com.fooddelivery.fooddeliveryfujitsu.entity.Station;
import com.fooddelivery.fooddeliveryfujitsu.entity.Weather;
import com.fooddelivery.fooddeliveryfujitsu.exception.WeatherDataFetchException;
import com.fooddelivery.fooddeliveryfujitsu.repository.StationRepository;
import com.fooddelivery.fooddeliveryfujitsu.repository.WeatherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class WeatherService {

    private final WeatherRepository weatherRepository;
    private final StationRepository stationRepository;
    private static final Logger log = LoggerFactory.getLogger(WeatherService.class);
    private static final String API_URL = "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php";
    private static final HttpClient httpClient = HttpClient.newHttpClient();


    @Scheduled(cron = "${weather.import.cron}")
    private void getWeatherFromIlmateenistus() {
        log.info("Fetching weather from Ilmateenistus.ee");

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.error("Failed to fetch weather data. HTTP Status: {}", response.statusCode());
                throw new WeatherDataFetchException("Failed to fetch weather data: HTTP " + response.statusCode());
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(response.body())));
            doc.getDocumentElement().normalize();

            String timestampStr = doc.getDocumentElement().getAttribute("timestamp");
            LocalDateTime timestamp = convertUnixTimestamp(Long.parseLong(timestampStr));
            log.info("Weather observation timestamp: {}", timestamp);

            NodeList stationList = doc.getElementsByTagName("station");

            Map<String, Station> stations = stationRepository.findAll().stream()
                    .collect(Collectors.toMap(Station::getStationName, station -> station));

            for (int i = 0; i < stationList.getLength(); i++) {
                Element stationElement = (Element) stationList.item(i);

                String stationName = stationElement.getElementsByTagName("name").item(0).getTextContent();

                if (stations.containsKey(stationName)) {
                    Station station = stations.get(stationName);
                    String wmoCode = stationElement.getElementsByTagName("wmocode").item(0).getTextContent();
                    Double airTemperature = checkDouble(stationElement.getElementsByTagName("airtemperature").item(0));
                    Double windSpeed = checkDouble(stationElement.getElementsByTagName("windspeed").item(0));
                    String weatherPhenomenon = stationElement.getElementsByTagName("phenomenon").item(0).getTextContent();

                    Weather weather = new Weather();
                    weather.setStation(station);
                    weather.setWmoCode(wmoCode);
                    weather.setAirTemperature(airTemperature);
                    weather.setWindSpeed(windSpeed);
                    weather.setWeatherPhenomenon(weatherPhenomenon);
                    weather.setTimestamp(timestamp);
                    weatherRepository.save(weather);
                    log.info("Saved weather data for station: {}", stationName);
                }
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WeatherDataFetchException("Failed to fetch weather data from the API", e);
        } catch (Exception e) {
            throw new WeatherDataFetchException("Failed to fetch weather data from the API", e);
        }
    }

    private LocalDateTime convertUnixTimestamp(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
    }

    private Double checkDouble(Node node) {
        if (node == null || node.getTextContent().trim().isEmpty()) {
            return null;
        }
        return Double.parseDouble(node.getTextContent());
    }
}
