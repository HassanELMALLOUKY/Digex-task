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
    public Object getOSM_ID(String cityName) throws Exception {
        int osmID = 0;
        String templateForObject = restTemplate.getForObject("https://nominatim.openstreetmap.org/search.php?q=" + cityName + "&polygon_geojson=1&format=jsonv2", String.class);
        // Create an ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Parse the JSON array
            JsonNode[] jsonNodes = objectMapper.readValue(templateForObject, JsonNode[].class);

            // Search for the key and get the value
            String searchKey = "osm_id";
            String searchValue = null;
            for (JsonNode node : jsonNodes) {
                if (node.has(searchKey)) {
                    searchValue = node.get(searchKey).asText();
                    osmID=Integer.parseInt(searchValue);
                    break;
                }
            }

            if (searchValue != null) {
                System.out.println("Found key: " + searchKey + ", Value: " + searchValue);
                String url="https://polygons.openstreetmap.fr/get_geojson.py?id="+osmID+"&params=0.000000-0.001000-0.001000";
                String result = restTemplate.getForObject(url, String.class);
                Polygon polygon=jsonMapper.fromJson(result,Polygon.class);
                return polygon;
            } else {
                System.out.println("Key not found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.format("City Not found");
    }
}
