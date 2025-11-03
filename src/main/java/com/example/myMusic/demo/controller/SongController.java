package com.example.myMusic.demo.controller;


import com.example.myMusic.demo.model.SongModel;
import com.example.myMusic.demo.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/songs")
@CrossOrigin(origins = "*")
public class SongController {

    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    @GetMapping("/hip-hop")
    public List<SongModel> getHiphopSongs() {
        return songService.getHiphopSongs();
    }
}
