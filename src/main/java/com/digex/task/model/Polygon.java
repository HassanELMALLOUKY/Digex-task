package com.digex.task.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Polygon {
    private int osm_id;
    private String type;
    private List<List<List<Double>> > coordinates;

}
