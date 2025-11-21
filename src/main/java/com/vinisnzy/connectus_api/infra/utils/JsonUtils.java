package com.vinisnzy.connectus_api.infra.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JsonUtils {

    private JsonUtils() {
        throw new IllegalStateException("Utility class");
    }

    private static final ObjectMapper mapper = new ObjectMapper();

    public static Map<String, Object> mergeMapWithJsonString(Map<String, Object> baseMap, String updateJson) {

        // Converte o Map existente para JsonNode
        JsonNode baseNode = mapper.valueToTree(baseMap);

        // Converte o novo JSON em string recebido para JsonNode
        JsonNode updateNode;
        try {
            updateNode = mapper.readTree(updateJson);
        } catch (Exception e) {
            throw new IllegalArgumentException("Configurações inválidas: " + e.getMessage());
        }

        JsonNode merged = deepMerge(baseNode, updateNode);

        return mapper.convertValue(merged, Map.class);
    }

    public static void validatePlanLimitsJson(Map<String, Object> limitsJson) {
        Map<String, Object> schemaMap = new HashMap<>();
        schemaMap.put("type", "object"); // alterar após definir o schema correto

        validateJson(limitsJson, schemaMap);
    }

    public static void validatePlanFeaturesJson(Map<String, Object> featuresJson) {
        Map<String, Object> schemaMap = new HashMap<>();
        schemaMap.put("type", "object"); // alterar após definir o schema correto

        validateJson(featuresJson, schemaMap);
    }

    public static void validateRolePermissionsJson(Map<String, Map<String, Boolean>> permissionsJson) {
        Map<String, Object> schemaMap = new HashMap<>();
        schemaMap.put("type", "object"); // alterar após definir o schema correto

        Schema schema = SchemaLoader.load(new JSONObject(permissionsJson));
        try {
            schema.validate(schemaMap);
        } catch (ValidationException e) {
            throw new IllegalArgumentException("Json inválido : " + e.getMessage());
        }
    }

    // Merge recursivo usando a árvore JSON
    private static JsonNode deepMerge(JsonNode base, JsonNode update) {
        if (base.isObject() && update.isObject()) {
            ObjectNode baseObj = (ObjectNode) base;
            ObjectNode updateObj = (ObjectNode) update;

            updateObj.properties().forEach(entry -> {
                String key = entry.getKey();
                JsonNode updateValue = entry.getValue();

                if (baseObj.has(key)) {
                    baseObj.set(key, deepMerge(baseObj.get(key), updateValue));
                } else {
                    baseObj.set(key, updateValue);
                }
            });
        }
        return base;
    }

    private static void validateJson(Map<String, Object> json, Map<String, Object> schemaMap) {
        Schema schema = SchemaLoader.load(new JSONObject(json));
        try {
            schema.validate(schemaMap);
        } catch (ValidationException e) {
            throw new IllegalArgumentException("Json inválido : " + e.getMessage());
        }
    }

}
