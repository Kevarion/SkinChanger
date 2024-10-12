package net.kevarion.skinChanger.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.List;

@CommandAlias("skin")
public class SkinCommand extends BaseCommand {

    private static String PROFILE_URL = "https://api.mojang.com/users/profiles/minecraft/";
    private static String SKIN_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";

    @Default
    public void main(Player player, String[] args) {

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Use: /skin (playerName)");
            return;
        }

        String targetSkin = args[0];
        PlayerProfile playerProfile = (PlayerProfile) player.getPlayerProfile();
        playerProfile.setProperties(getTextureProperty(targetSkin));
        player.setPlayerProfile(playerProfile);

        player.sendMessage(ChatColor.GREEN + "You have changed your skin to " + targetSkin + "'s skin!");

    }

    private Collection<ProfileProperty> getTextureProperty(String targetSkin) {
        String profileResponse = makeRequest(PROFILE_URL + targetSkin);
        JsonObject profileObject = JsonParser.parseString(profileResponse).getAsJsonObject();
        String uuid = profileObject.get("id").getAsString();

        String skinResponse = makeRequest(SKIN_URL.formatted(uuid));
        JsonObject skinObject = JsonParser.parseString(skinResponse).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
        String value = skinObject.get("value").getAsString();
        String signature = skinObject.get("signature").getAsString();

        return List.of(new ProfileProperty("textures", value, signature));
    }

    private String makeRequest(String url) {
        try (HttpClient httpClient = HttpClient.newBuilder().build()) {
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
