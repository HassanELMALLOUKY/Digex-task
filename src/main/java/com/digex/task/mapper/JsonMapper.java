package com.digex.task.mapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class JsonMapper {
    private final ObjectMapper objectMapper;

    public JsonMapper() {
        this.objectMapper = new ObjectMapper();
    }

    // Convert JSON to the model
    public <T> T fromJson(String json,Class<T> valueType) throws Exception {
        return objectMapper.readValue(json, valueType);
    }

    // Convert the model to JSON
    public String toJson(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }
}

