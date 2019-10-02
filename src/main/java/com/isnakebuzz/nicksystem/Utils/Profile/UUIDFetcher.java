package com.isnakebuzz.nicksystem.Utils.Profile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;

public class UUIDFetcher {
    private static String profile_url;
    private static HashMap<String, UUID> cache;

    static {
        UUIDFetcher.profile_url = "https://api.mojang.com/users/profiles/minecraft/";
        UUIDFetcher.cache = new HashMap<String, UUID>();
    }

    public static UUID fetchUUID(final String name) throws Exception {
        if (UUIDFetcher.cache.containsKey(name)) {
            return UUIDFetcher.cache.get(name);
        }
        final URL url = new URL(String.valueOf(UUIDFetcher.profile_url) + name);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        final String line = reader.readLine();
        final String[] input = line.split(":");
        String[] id = null;
        if (input.length > 8) {
            id = input[2].split(",");
        } else {
            id = input[1].split(",");
        }
        final String uuid = id[0].replace("\"", "");
        final UUID finaluuid = UUID.fromString(String.valueOf(uuid.substring(0, 8)) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-" + uuid.substring(16, 20) + "-" + uuid.substring(20, 32));
        return finaluuid;
    }
}
