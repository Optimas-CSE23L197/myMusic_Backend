package com.example.myMusic.demo.controller;

import com.example.myMusic.demo.service.FaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/face")
@CrossOrigin(origins = "*")
public class FaceController {

    private final FaceService faceService;

    public FaceController(FaceService faceService) {
        this.faceService = faceService;
    }

//    post mapping for face upload
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFace(@RequestParam("image")MultipartFile image) {
        try {
            Map<String,Object> result = faceService.processImage(image);
            System.out.println(result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
