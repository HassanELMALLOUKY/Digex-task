package com.digex.task.controller;

import com.digex.task.model.Polygon;
import com.digex.task.service.IPolygonService;
import org.springframework.web.bind.annotation.*;

@RestController
public class PolygonController {
    final private IPolygonService polygonService;

    public PolygonController(IPolygonService polygonService) {
        this.polygonService = polygonService;
    }

    @GetMapping("/polygon")
    Object getPolygons(@RequestParam(name = "city") String cityName) throws Exception {
        return polygonService.getOSM_ID(cityName);
    }
}
