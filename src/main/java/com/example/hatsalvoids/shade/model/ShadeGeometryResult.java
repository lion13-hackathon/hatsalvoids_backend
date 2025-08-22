package com.example.hatsalvoids.shade.model;

import java.util.List;

public record ShadeGeometryResult(List<List<double[]>> originalRings, List<List<double[]>> shadeRings) {
}
