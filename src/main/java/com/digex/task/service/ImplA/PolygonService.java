package com.digex.task.service.ImplA;

import com.digex.task.mapper.JsonMapper;
import com.digex.task.model.Polygon;
import com.digex.task.service.IPolygonService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PolygonService implements IPolygonService {
    final
    private JsonMapper jsonMapper;
    private final RestTemplate restTemplate;

    public PolygonService(JsonMapper jsonMapper, RestTemplate restTemplate) {
        this.jsonMapper = jsonMapper;
        this.restTemplate = restTemplate;
    }


    @Override
    public Object getOSM_ID(String cityName, String subpart) throws Exception {
        int osmID = 0;
        String templateForObject = restTemplate.getForObject("https://nominatim.openstreetmap.org/search.php?q=" + subpart + "&polygon_geojson=1&format=jsonv2", String.class);
        // Create an ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Parse the JSON array
            JsonNode[] jsonNodes = objectMapper.readValue(templateForObject, JsonNode[].class);

            // Search for the key and get the value
            String searchKey = "osm_id";
            String searchValue = null;
            for (JsonNode node : jsonNodes) {
                if (node.findValue("display_name").toString().contains(cityName)) {
                    System.out.println("geojson: "+node.findValue("geojson").toString());
                    Polygon polygon=jsonMapper.fromJson(node.findValue("geojson").toString(), Polygon.class);
                    polygon.setOsm_id(node.findValue("osm_id").asInt());
                    return polygon;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.format("City Not found");
    }
}
