package com.example.myMusic.demo.service;

import com.example.myMusic.demo.util.MultipartInputStreamFileResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class FaceService {

    @Value("${ml.server.url}")
    private String mlServerUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> processImage(MultipartFile image) throws IOException {
        System.out.println("📸 Received image in FaceService: " + image.getOriginalFilename());

        // Prepare request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", new MultipartInputStreamFileResource(image.getInputStream(), image.getOriginalFilename()));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            System.out.println("🚀 Sending image to ML server at: " + mlServerUrl);

            ResponseEntity<Map> response = restTemplate.postForEntity(mlServerUrl, requestEntity, Map.class);

            System.out.println("✅ Flask responded with status: " + response.getStatusCode());
            System.out.println("🧠 Raw response body: " + response.getBody());

            Map<String, Object> mlResult = response.getBody();
            if (mlResult == null || !mlResult.containsKey("mood")) {
                throw new RuntimeException("Invalid response from ML server: " + mlResult);
            }

            String mood = (String) mlResult.get("mood");
            Double confidence = mlResult.containsKey("confidence") ? (Double) mlResult.get("confidence") : null;

            // Get correct song JSON file path
            String songPath = getSongForMood(mood);
            Map<String, Object> songData = readJsonAsMap(songPath);

            // ✅ Build final structured JSON response
            Map<String, Object> result = new HashMap<>();
            result.put("mood", mood);
            result.put("confidence", confidence);
            result.put("songs", songData);

            System.out.println("🎵 Final JSON sent to frontend: " + result);
            return result;

        } catch (Exception e) {
            System.err.println("❌ Error communicating with ML server: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to process image: " + e.getMessage());
        }
    }

    // ✅ Reads JSON file and converts to Map (not string)
    private Map<String, Object> readJsonAsMap(String path) {
        try (InputStream inputStream = getClass().getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new RuntimeException("File not found: " + path);
            }
            String jsonText = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return objectMapper.readValue(jsonText, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    private String getSongForMood(String mood) {
        Map<String, String> moodToSong = new HashMap<>();
        moodToSong.put("happy", "/data/HipHop.json");
        moodToSong.put("sad", "/data/sad.json");
        moodToSong.put("angry", "/data/angry.json");
        moodToSong.put("neutral", "/data/neutral.json");
        return moodToSong.getOrDefault(mood.toLowerCase(), "/data/neutral.json");
    }
}
