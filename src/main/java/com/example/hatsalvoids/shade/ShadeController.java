package com.example.hatsalvoids.shade;


import com.example.hatsalvoids.global.success.SuccessResponse;
import com.example.hatsalvoids.shade.model.FetchShadeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/shades")
@RequiredArgsConstructor
public class ShadeController {
    private final ShadeService shadeService;

    @CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500"})
    @GetMapping
    public ResponseEntity<SuccessResponse<List<FetchShadeResponse>>> getShades(@RequestParam String latitude,
                                                                               @RequestParam String longitude,
                                                                               @RequestParam double radius,
                                                                               @RequestParam String time,
                                                                               @RequestParam String zoneId) {
        return ResponseEntity
                .status(ShadeSuccessCode.SHADE_FETCHED.getStatus())
                .body(SuccessResponse.of(ShadeSuccessCode.SHADE_FETCHED,
                        shadeService.getShades(latitude, longitude, radius, time, zoneId)));
    }
}
