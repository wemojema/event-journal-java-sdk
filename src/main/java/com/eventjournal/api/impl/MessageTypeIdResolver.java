package com.eventjournal.api.impl;

import com.eventjournal.api.Message;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class MessageTypeIdResolver implements TypeIdResolver {
    private static final Logger log = LoggerFactory.getLogger(MessageTypeIdResolver.class);
    private static JavaType superType;
    private static Map<String, Class<? extends Message>> typeMap;

    public static void scanForTypes(ObjectMapper mapper) {
        superType = mapper.getTypeFactory().constructType(Message.class);
        typeMap = new HashMap<>();

        log.trace("Scanning for Event and Command types");

        Arrays.stream(Package.getPackages())
                .map(p -> p.getName().split("\\.")[0])
                .distinct()
                .filter(p -> p != null && !p.isEmpty())
                .peek(p -> log.trace("Scanning Package: " + p))
                .flatMap(topLevelPackage -> identifyMessageTypes(topLevelPackage).stream())
                .peek(c -> log.trace("Found Message Type: " + c.getSimpleName()))
                .forEach(MessageTypeIdResolver::addType);

        log.trace("Scanning for Event and Command types complete, found: " + MessageTypeIdResolver.availableTypes().size() + " types.");
    }


    private static Set<Class<? extends Message>> identifyMessageTypes(String topLevelPackage) {
        return new Reflections(topLevelPackage).getSubTypesOf(Message.class);
    }

    public static void addType(Class<? extends Message> clazz) {
        typeMap.put(clazz.getSimpleName(), clazz);
    }

    public static Collection<Object> availableTypes() {
        return Arrays.asList(typeMap.values().toArray());
    }


    @Override
    public void init(JavaType javaType) {

    }

    @Override
    public String idFromBaseType() {
        return superType.getClass().getSimpleName();
    }

    @Override
    public String getDescForKnownTypeIds() {
        return typeMap.toString();
    }

    @Override
    public String idFromValue(Object value) {
        return idFromValueAndType(value, value.getClass());
    }

    @Override
    public String idFromValueAndType(Object value, Class<?> suggestedType) {
        return suggestedType.getSimpleName();
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) throws IOException {
        Class<? extends Message> clazz = typeMap.get(id);
        if (clazz == null) {
            throw new InvalidTypeIdException(null, "Unknown type id: " + id, superType, id);
        }
        return context.constructSpecializedType(superType, clazz);
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }
}
