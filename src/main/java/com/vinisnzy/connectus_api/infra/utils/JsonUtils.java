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

    private static final Map<String, Map<String, Boolean>> DEFAULT_ROLE_PERMISSIONS = Map.ofEntries(
            Map.entry("users", Map.of("view", false, "create", false, "edit", false, "delete", false)),
            Map.entry("logs", Map.of("view", false)),
            Map.entry("whatsapp_connection", Map.of("connect", false, "disconnect", false, "create", false, "view", false, "edit", false, "delete", false)),
            Map.entry("subscription", Map.of("view", false, "edit", false, "cancel", false, "renew", false, "suspend", false, "reactivate", false)),
            Map.entry("quick_replies", Map.of("view", false, "create", false, "edit", false, "delete", false)),
            Map.entry("company", Map.of("delete", false, "toggle_active", false, "edit_settings", false)),
            Map.entry("plans", Map.of("create", false, "edit", false, "delete", false, "toggle_active", false)),
            Map.entry("roles", Map.of("create", false, "edit", false, "delete", false, "toggle_active", false)),
            Map.entry("contacts", Map.of("create", false, "edit", false, "delete", false, "toggle_block", false, "add_tags", false, "remove_tags", false, "import", false)),
            Map.entry("contact_groups", Map.of("create", false, "edit", false, "delete", false)),
            Map.entry("messages", Map.of("send_message", false, "delete", false)),
            Map.entry("tickets", Map.of("create", false, "edit", false, "resolve", false, "close", false, "reopen", false, "set_pending", false, "archive", false, "unarchive", false, "add_tags", false)),
            Map.entry("ticket_tags", Map.of("create", false, "edit", false, "delete", false)),
            Map.entry("appointments", Map.of("create", false, "edit", false, "confirm", false, "complete", false, "cancel", false, "mark_no_show", false, "reschedule", false, "send_reminder", false)),
            Map.entry("services", Map.of("create", false, "edit", false, "delete", false, "toggle_active", false))
    );


    private static final ObjectMapper mapper = new ObjectMapper();

    public static Map<String, Map<String, Boolean>> mergeRolePermissions(Map<String, Map<String, Boolean>> updateJson) {
        JsonNode baseNode = mapper.valueToTree(DEFAULT_ROLE_PERMISSIONS);

        JsonNode updateNode;
        try {
            String updateJsonString = mapper.writeValueAsString(updateJson);
            updateNode = mapper.readTree(updateJsonString);
        } catch (Exception e) {
            throw new IllegalArgumentException("Configurações inválidas: " + e.getMessage());
        }

        JsonNode merged = deepMerge(baseNode, updateNode);

        return mapper.convertValue(merged, Map.class);
    }

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
        JsonNode defaultNode = mapper.valueToTree(DEFAULT_ROLE_PERMISSIONS);
        JsonNode updateNode = mapper.valueToTree(permissionsJson);
        JsonNode merged = deepMerge(defaultNode.deepCopy(), updateNode);
        validateAgainstPermissionSchema(merged);
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
                    JsonNode mergedValue = deepMerge(baseObj.get(key), updateValue);
                    baseObj.set(key, mergedValue);
                } else {
                    baseObj.set(key, updateValue);
                }
            });

            return baseObj;
        }

        return update;
    }

    private static void validateJson(Map<String, Object> json, Map<String, Object> schemaMap) {
        JSONObject schemaJson = new JSONObject(schemaMap);
        JSONObject jsonObject = new JSONObject(json);
        Schema schema = SchemaLoader.load(schemaJson);
        try {
            schema.validate(jsonObject);
        } catch (ValidationException e) {
            throw new IllegalArgumentException("Json inválido : " + e.getMessage());
        }
    }

    private static void validateAgainstPermissionSchema(JsonNode jsonNode) {
        Map<String, Object> schemaMap = buildPermissionsSchema();
        JSONObject schemaJson = new JSONObject(schemaMap);
        JSONObject jsonObject = new JSONObject(mapper.convertValue(jsonNode, Map.class));
        Schema schema = SchemaLoader.load(schemaJson);
        try {
            schema.validate(jsonObject);
        } catch (ValidationException e) {
            throw new IllegalArgumentException("Json inválido: " + e.getMessage());
        }
    }

    private static Map<String, Object> buildPermissionsSchema() {
        return Map.ofEntries(
                Map.entry("type", "object"),
                Map.entry("additionalProperties", false),
                Map.entry("properties", Map.ofEntries(

                        // USERS
                        Map.entry("users", Map.of(
                                "type", "object",
                                "additionalProperties", false,
                                "properties", Map.ofEntries(
                                        Map.entry("view", Map.of("type", "boolean")),
                                        Map.entry("create", Map.of("type", "boolean")),
                                        Map.entry("edit", Map.of("type", "boolean")),
                                        Map.entry("delete", Map.of("type", "boolean"))
                                )
                        )),

                        // LOGS
                        Map.entry("logs", Map.of(
                                "type", "object",
                                "additionalProperties", false,
                                "properties", Map.of("view", Map.of("type", "boolean"))
                        )),

                        // WHATSAPP CONNECTION
                        Map.entry("whatsapp_connection", Map.of(
                                "type", "object",
                                "additionalProperties", false,
                                "properties", Map.ofEntries(
                                        Map.entry("connect", Map.of("type", "boolean")),
                                        Map.entry("disconnect", Map.of("type", "boolean")),
                                        Map.entry("create", Map.of("type", "boolean")),
                                        Map.entry("view", Map.of("type", "boolean")),
                                        Map.entry("edit", Map.of("type", "boolean")),
                                        Map.entry("delete", Map.of("type", "boolean"))
                                )
                        )),

                        // SUBSCRIPTION
                        Map.entry("subscription", Map.of(
                                "type", "object",
                                "additionalProperties", false,
                                "properties", Map.ofEntries(
                                        Map.entry("view", Map.of("type", "boolean")),
                                        Map.entry("edit", Map.of("type", "boolean")),
                                        Map.entry("cancel", Map.of("type", "boolean")),
                                        Map.entry("renew", Map.of("type", "boolean")),
                                        Map.entry("suspend", Map.of("type", "boolean")),
                                        Map.entry("reactivate", Map.of("type", "boolean"))
                                )
                        )),

                        // QUICK REPLIES
                        Map.entry("quick_replies", Map.of(
                                "type", "object",
                                "additionalProperties", false,
                                "properties", Map.ofEntries(
                                        Map.entry("view", Map.of("type", "boolean")),
                                        Map.entry("create", Map.of("type", "boolean")),
                                        Map.entry("edit", Map.of("type", "boolean")),
                                        Map.entry("delete", Map.of("type", "boolean"))
                                )
                        )),

                        // COMPANY
                        Map.entry("company", Map.of(
                                "type", "object",
                                "additionalProperties", false,
                                "properties", Map.ofEntries(
                                        Map.entry("delete", Map.of("type", "boolean")),
                                        Map.entry("toggle_active", Map.of("type", "boolean")),
                                        Map.entry("edit_settings", Map.of("type", "boolean"))
                                )
                        )),

                        // PLANS
                        Map.entry("plans", Map.of(
                                "type", "object",
                                "additionalProperties", false,
                                "properties", Map.ofEntries(
                                        Map.entry("create", Map.of("type", "boolean")),
                                        Map.entry("edit", Map.of("type", "boolean")),
                                        Map.entry("delete", Map.of("type", "boolean")),
                                        Map.entry("toggle_active", Map.of("type", "boolean"))
                                )
                        )),

                        // ROLES
                        Map.entry("roles", Map.of(
                                "type", "object",
                                "additionalProperties", false,
                                "properties", Map.ofEntries(
                                        Map.entry("create", Map.of("type", "boolean")),
                                        Map.entry("edit", Map.of("type", "boolean")),
                                        Map.entry("delete", Map.of("type", "boolean")),
                                        Map.entry("toggle_active", Map.of("type", "boolean"))
                                )
                        )),

                        // CONTACTS
                        Map.entry("contacts", Map.of(
                                "type", "object",
                                "additionalProperties", false,
                                "properties", Map.ofEntries(
                                        Map.entry("create", Map.of("type", "boolean")),
                                        Map.entry("edit", Map.of("type", "boolean")),
                                        Map.entry("delete", Map.of("type", "boolean")),
                                        Map.entry("toggle_block", Map.of("type", "boolean")),
                                        Map.entry("add_tags", Map.of("type", "boolean")),
                                        Map.entry("remove_tags", Map.of("type", "boolean")),
                                        Map.entry("import", Map.of("type", "boolean"))
                                )
                        )),

                        // CONTACT GROUPS
                        Map.entry("contact_groups", Map.of(
                                "type", "object",
                                "additionalProperties", false,
                                "properties", Map.ofEntries(
                                        Map.entry("create", Map.of("type", "boolean")),
                                        Map.entry("edit", Map.of("type", "boolean")),
                                        Map.entry("delete", Map.of("type", "boolean"))
                                )
                        )),

                        // MESSAGES
                        Map.entry("messages", Map.of(
                                "type", "object",
                                "additionalProperties", false,
                                "properties", Map.ofEntries(
                                        Map.entry("send_message", Map.of("type", "boolean")),
                                        Map.entry("delete", Map.of("type", "boolean"))
                                )
                        )),

                        // TICKETS
                        Map.entry("tickets", Map.of(
                                "type", "object",
                                "additionalProperties", false,
                                "properties", Map.ofEntries(
                                        Map.entry("create", Map.of("type", "boolean")),
                                        Map.entry("edit", Map.of("type", "boolean")),
                                        Map.entry("resolve", Map.of("type", "boolean")),
                                        Map.entry("close", Map.of("type", "boolean")),
                                        Map.entry("reopen", Map.of("type", "boolean")),
                                        Map.entry("set_pending", Map.of("type", "boolean")),
                                        Map.entry("archive", Map.of("type", "boolean")),
                                        Map.entry("unarchive", Map.of("type", "boolean")),
                                        Map.entry("add_tags", Map.of("type", "boolean"))
                                )
                        )),

                        // TICKET TAGS
                        Map.entry("ticket_tags", Map.of(
                                "type", "object",
                                "additionalProperties", false,
                                "properties", Map.ofEntries(
                                        Map.entry("create", Map.of("type", "boolean")),
                                        Map.entry("edit", Map.of("type", "boolean")),
                                        Map.entry("delete", Map.of("type", "boolean"))
                                )
                        )),

                        // APPOINTMENTS
                        Map.entry("appointments", Map.of(
                                "type", "object",
                                "additionalProperties", false,
                                "properties", Map.ofEntries(
                                        Map.entry("create", Map.of("type", "boolean")),
                                        Map.entry("edit", Map.of("type", "boolean")),
                                        Map.entry("confirm", Map.of("type", "boolean")),
                                        Map.entry("complete", Map.of("type", "boolean")),
                                        Map.entry("cancel", Map.of("type", "boolean")),
                                        Map.entry("mark_no_show", Map.of("type", "boolean")),
                                        Map.entry("reschedule", Map.of("type", "boolean")),
                                        Map.entry("send_reminder", Map.of("type", "boolean"))
                                )
                        )),

                        // SERVICES
                        Map.entry("services", Map.of(
                                "type", "object",
                                "additionalProperties", false,
                                "properties", Map.ofEntries(
                                        Map.entry("create", Map.of("type", "boolean")),
                                        Map.entry("edit", Map.of("type", "boolean")),
                                        Map.entry("delete", Map.of("type", "boolean")),
                                        Map.entry("toggle_active", Map.of("type", "boolean"))
                                )
                        ))

                ))
        );
    }


}
