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
        // Create an ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String templateForObject = restTemplate.getForObject("https://nominatim.openstreetmap.org/search.php?q=" + cityName + "&polygon_geojson=1&format=jsonv2", String.class);
            // Parse the JSON array
            JsonNode jsonNode = objectMapper.readValue(templateForObject, JsonNode.class);
            long osm_id = jsonNode.findValue("osm_id").asInt();
            System.out.println("city osm_id = " + osm_id);
            String subpartResponse=restTemplate.getForObject("https://nominatim.openstreetmap.org/search.php?q=" + subpart + "&polygon_geojson=1&format=jsonv2", String.class);
            JsonNode[] subpartJsonNodes = objectMapper.readValue(subpartResponse, JsonNode[].class);

            // Search for the key and get the value
            String searchKey = "osm_id";
            long searchValue = 0;
            for (JsonNode node : subpartJsonNodes) {
                long tryOsmID=node.findValue("osm_id").asLong();
                String osmType="";
                String categoryType=node.findValue("category").asText();
                if (node.findValue("osm_type").asText().equals("relation"))
                    osmType="R";
                else osmType = "N";
                System.out.println("osmType = " + osmType);
                System.out.println("place rank"+node.findValue("place_rank").asInt());
                System.out.println("tryOsmID = " + tryOsmID);
                JsonNode TryPlaces = restTemplate.getForObject("https://nominatim.openstreetmap.org/details.php?osmtype="+osmType+"&osmid=" + tryOsmID + "&class="+categoryType+"&addressdetails=1&hierarchy=0&group_hierarchy=1&polygon_geojson=1&format=json", JsonNode.class);
                JsonNode Addresses = TryPlaces.findValue("address");
                //System.out.println("Addresses = " + Addresses);
                for (JsonNode address : Addresses) {
                    System.out.println("address = " + address);
                    if(address.findValue("osm_id")==null) continue;
                    if (address.findValue("osm_id").asLong() == osm_id) {
                        // Found the object with the desired osm_id
                        System.out.println("condition true");
                        searchValue = tryOsmID;
                        System.out.println("finish: "+tryOsmID);
                        Polygon polygon=new Polygon();
                        polygon=jsonMapper.fromJson(TryPlaces.findValue("geometry").toString(),Polygon.class);
                        polygon.setOsm_id(searchValue);
                        return polygon;
                    }

                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.format("City Not found");
    }
}
