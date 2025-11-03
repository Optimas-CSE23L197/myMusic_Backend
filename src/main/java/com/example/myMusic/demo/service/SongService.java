package com.example.myMusic.demo.service;

import com.example.myMusic.demo.model.SongModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

@Service
public class SongService {

    public List<SongModel> getHiphopSongs() {
        try (InputStreamReader reader = new InputStreamReader(
                getClass().getResourceAsStream("/data/HipHop.json")
        )) {
            if (reader == null) {
                throw new IllegalStateException("hiphop.json not found in /resources/data/");
            }
            System.out.println("/data/HipHop.json");
            Type type = new TypeToken<Map<String, List<SongModel>>>() {}.getType();
            Map<String, List<SongModel>> data = new Gson().fromJson(reader, type);
            return data.getOrDefault("hiphop", List.of());

        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // Return empty list instead of null to prevent controller null pointer
        }
    }

}
