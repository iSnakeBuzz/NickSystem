package com.isnakebuzz.nicksystem.Utils.Profile;

import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.util.UUIDTypeAdapter;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class GameProfileBuilder {
    private static final String SERVICE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";
    private static final String JSON_SKIN = "{\"timestamp\":%d,\"profileId\":\"%s\",\"profileName\":\"%s\",\"isPublic\":true,\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}";
    private static final String JSON_CAPE = "{\"timestamp\":%d,\"profileId\":\"%s\",\"profileName\":\"%s\",\"isPublic\":true,\"textures\":{\"SKIN\":{\"url\":\"%s\"},\"CAPE\":{\"url\":\"%s\"}}}";
    private static Gson gson;
    private static HashMap<UUID, CachedProfile> cache;
    private static long cacheTime;

    static {
        GameProfileBuilder.gson = new GsonBuilder().disableHtmlEscaping().registerTypeAdapter((Type) UUID.class, (Object) new UUIDTypeAdapter()).registerTypeAdapter((Type) GameProfile.class, (Object) new GameProfileSerializer()).registerTypeAdapter((Type) PropertyMap.class, (Object) new PropertyMap.Serializer()).create();
        GameProfileBuilder.cache = new HashMap<UUID, CachedProfile>();
        GameProfileBuilder.cacheTime = -1L;
    }

    public static GameProfile fetch(final UUID uuid) throws IOException {
        return fetch(uuid, false);
    }

    public static GameProfile fetch(final UUID uuid, final boolean forceNew) throws IOException {
        if (!forceNew && GameProfileBuilder.cache.containsKey(uuid) && GameProfileBuilder.cache.get(uuid).isValid()) {
            return GameProfileBuilder.cache.get(uuid).profile;
        }
        final HttpURLConnection connection = (HttpURLConnection) new URL(String.format("https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false", UUIDTypeAdapter.fromUUID(uuid))).openConnection();
        connection.setReadTimeout(5000);
        if (connection.getResponseCode() == 200) {
            final String json = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
            final GameProfile result = (GameProfile) GameProfileBuilder.gson.fromJson(json, (Class) GameProfile.class);
            GameProfileBuilder.cache.put(uuid, new CachedProfile(result));
            return result;
        }
        if (!forceNew && GameProfileBuilder.cache.containsKey(uuid)) {
            return GameProfileBuilder.cache.get(uuid).profile;
        }
        final JsonObject error = (JsonObject) new JsonParser().parse(new BufferedReader(new InputStreamReader(connection.getErrorStream())).readLine());
        throw new IOException(String.valueOf(error.get("error").getAsString()) + ": " + error.get("errorMessage").getAsString());
    }

    public static GameProfile getProfile(final UUID uuid, final String name, final String skin) {
        return getProfile(uuid, name, skin, null);
    }

    public static GameProfile getProfile(final UUID uuid, final String name, final String skinUrl, final String capeUrl) {
        final GameProfile profile = new GameProfile(uuid, name);
        final boolean cape = capeUrl != null && !capeUrl.isEmpty();
        final List<Object> args = new ArrayList<Object>();
        args.add(System.currentTimeMillis());
        args.add(UUIDTypeAdapter.fromUUID(uuid));
        args.add(name);
        args.add(skinUrl);
        if (cape) {
            args.add(capeUrl);
        }
        profile.getProperties().put("textures", new Property("textures", Base64Coder.encodeString(String.format(cape ? "{\"timestamp\":%d,\"profileId\":\"%s\",\"profileName\":\"%s\",\"isPublic\":true,\"textures\":{\"SKIN\":{\"url\":\"%s\"},\"CAPE\":{\"url\":\"%s\"}}}" : "{\"timestamp\":%d,\"profileId\":\"%s\",\"profileName\":\"%s\",\"isPublic\":true,\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}", args.toArray(new Object[args.size()])))));
        return profile;
    }

    public static void setCacheTime(final long time) {
        GameProfileBuilder.cacheTime = time;
    }

    private static class GameProfileSerializer implements JsonSerializer<GameProfile>, JsonDeserializer<GameProfile> {
        public GameProfile deserialize(final JsonElement json, final Type type, final JsonDeserializationContext context) throws JsonParseException {
            final JsonObject object = (JsonObject) json;
            final UUID id = object.has("id") ? ((UUID) context.deserialize(object.get("id"), (Type) UUID.class)) : null;
            final String name = object.has("name") ? object.getAsJsonPrimitive("name").getAsString() : null;
            final GameProfile profile = new GameProfile(id, name);
            if (object.has("properties")) {
                for (final Map.Entry<String, Property> prop : ((PropertyMap) context.deserialize(object.get("properties"), (Type) PropertyMap.class)).entries()) {
                    profile.getProperties().put(prop.getKey(), prop.getValue());
                }
            }
            return profile;
        }

        public JsonElement serialize(final GameProfile profile, final Type type, final JsonSerializationContext context) {
            final JsonObject result = new JsonObject();
            if (profile.getId() != null) {
                result.add("id", context.serialize((Object) profile.getId()));
            }
            if (profile.getName() != null) {
                result.addProperty("name", profile.getName());
            }
            if (!profile.getProperties().isEmpty()) {
                result.add("properties", context.serialize((Object) profile.getProperties()));
            }
            return (JsonElement) result;
        }
    }

    private static class CachedProfile {
        private long timestamp;
        private GameProfile profile;

        public CachedProfile(final GameProfile profile) {
            this.timestamp = System.currentTimeMillis();
            this.profile = profile;
        }

        public boolean isValid() {
            return GameProfileBuilder.cacheTime < 0L || System.currentTimeMillis() - this.timestamp < GameProfileBuilder.cacheTime;
        }
    }
}
