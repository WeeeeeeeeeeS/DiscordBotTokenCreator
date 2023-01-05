package dev.wee;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import okhttp3.*;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PersonalDevAccount {
    private static final String API_URL = "https://discord.com/api/applications";
    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
    private static final Gson GSON = new Gson();
    private final String code;
    private final String token;
    private final TextChannel channel;

    public PersonalDevAccount(String selfToken, String selfSecret, TextChannel channel) {
        this.token = selfToken;
        this.code = selfSecret;
        this.channel = channel;
    }


    // creates as many bots you want *takes a lot of time*
    // returns tokens
    public void generateBots(String standardName, int amount) throws IOException {
        List<Bot> bots = new ArrayList<>();
        int c = 0;
        int teamsCounter = 1;
        Team team = createNewTeam(standardName + " : " + teamsCounter);
        for (int i = 0; i < amount; i++) {
            System.out.println("*********************");
            System.out.println("NOW CREATING: " + standardName + " : " + i);
            String botId = generateANewTeamApp(standardName + " " + i, team.getId());
            String botToken = resetBotTokenByAppId(botId);
            bots.add(new Bot(standardName + " : " + i, botId, botToken));
            c++;
            if (c >= 25) {
                System.out.println("TEAM IS FULL, CREATING A NEW ONE.");
                StringBuilder s = new StringBuilder();
                s.append("TEAM: ").append(team.getName()).append("\n\n");
                for (Bot bot : bots) {
                    s.append(bot).append("\n");
                }
                channel.sendMessage(s.toString()).queue();
                channel.sendMessage("``````").queue();
                bots.clear();
                teamsCounter++;
                team = createNewTeam(standardName + teamsCounter);
            }
            System.out.println("BOT CREATED: " + standardName + " : " + i);
            System.out.println("*********************");
        }
        if (!bots.isEmpty()) {
            StringBuilder s = new StringBuilder();
            s.append("TEAM: ").append(team.getName()).append("\n\n");
            for (Bot bot : bots) {
                s.append(bot).append("\n");
            }
            channel.sendMessage(s.toString()).queue();
            channel.sendMessage("``````").queue();
        }
    }

    public Team createNewTeam(String name) throws IOException {
        // Create the request body
        JsonObject data = new JsonObject();
        data.addProperty("name", name);
        RequestBody body = RequestBody.create(GSON.toJson(data), MediaType.get("application/json"));

        // Create the request
        Request request = new Request.Builder()
                .url("https://discord.com/api/v9/teams")
                .post(body)
                .addHeader("Authorization", token)
                .build();

        // Send the request and get the response
        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // Request was successful
                var responseBody = response.body();
                if (responseBody == null) {
                    System.out.println("Response body is null");
                    return null;
                }
                JsonObject json = GSON.fromJson(responseBody.string(), JsonObject.class);
                return GSON.fromJson(json, Team.class);
            } else {
                if (response.code() == 429) {
                    // Server returned a 429 response code, indicating that the client is being rate limited
                    String retryAfterHeader = response.headers().get("Retry-After");
                    if (retryAfterHeader != null) {
                        // Retry
                        // Retry-After header is present, parse the value to get the number of seconds to wait
                        long retryAfterSeconds = Long.parseLong(retryAfterHeader);
                        System.out.println("[CREATE NEW TEAM] Retrying request in " + retryAfterSeconds / 1000 + " seconds");
                        System.out.println("[GENERATE BOT] IN YOUR CURRENT TIME IT IS: " + Instant.now().plusMillis(retryAfterSeconds));
                        Thread.sleep(retryAfterSeconds);
                        return createNewTeam(name);
                    }
                } else {
                    // Request was not successful
                    for (int i = 0; i < 5; i++) {
                        System.out.println("INVALID SELF-TOKEN!");
                    }
                    System.out.println(response);
                    System.exit(0);
                    return null;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    // creates a new app & returns its id
    public String generateANewTeamApp(String name, String teamId) throws IOException {
        // Create the request body
        JsonObject data = new JsonObject();
        data.addProperty("name", name);
        data.addProperty("team_id", teamId);
        data.addProperty("bot_public", true);
        data.addProperty("bot_require_code_grant", false);
        data.addProperty("flags", 565248);
        if (code != null) {
            data.addProperty("code", TwoFactorAuth.generateCode(code));
        }
        RequestBody body = RequestBody.create(GSON.toJson(data), MediaType.get("application/json"));

        // Create the request
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Authorization", token)
                .build();

        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // Request was successful
                var responseBody = response.body();
                if (responseBody == null) {
                    System.out.println("Response body is null");
                    return null;
                }
                JsonObject json = GSON.fromJson(responseBody.string(), JsonObject.class);
                return json.get("id").getAsString();
            } else {
                if (response.code() == 429) {
                    // Server returned a 429 response code, indicating that the client is being rate limited
                    String retryAfterHeader = response.headers().get("Retry-After");
                    if (retryAfterHeader != null) {
                        // Retry
                        // Retry-After header is present, parse the value to get the number of seconds to wait
                        long retryAfterSeconds = Long.parseLong(retryAfterHeader);
                        System.out.println("[GENERATE BOT] Retrying in " + retryAfterSeconds / 1000 + " seconds");
                        System.out.println("[GENERATE BOT] IN YOUR CURRENT TIME IT IS: " + Instant.now().plusMillis(retryAfterSeconds));
                        try {
                            Thread.sleep(retryAfterSeconds);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return generateANewTeamApp(name, teamId);
                    }
                } else {
                    // Request failed
                    System.out.println("[GENERATE BOT] Request failed: " + response.code() + " " + response.message());
                    return null;
                }
            }
        }
        return null;
    }


    // resets the token of the app
    public String resetBotTokenByAppId(String applicationId) throws IOException {
        RequestBody body = RequestBody.create("{\"code\":\"" + TwoFactorAuth.generateCode(code) + "\"}", MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(API_URL + "/" + applicationId + "/bot/reset")
                .post(body)
                .addHeader("Authorization", token)
                .build();
        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // request was successful
                var responseBody = response.body();
                if (responseBody == null) {
                    System.out.println("Response body is null");
                    return null;
                }
                JsonObject json = GSON.fromJson(responseBody.string(), JsonObject.class);
                System.out.println(json.get("token").getAsString());
                return json.get("token").getAsString();
            } else {
                if (response.code() == 429) {
                    // Server returned a 429 response code, indicating that the client is being rate limited
                    String retryAfterHeader = response.headers().get("Retry-After");
                    if (retryAfterHeader != null) {
                        // Retry-After header is present, parse the value to get the number of seconds to wait
                        long retryAfterSeconds = Long.parseLong(retryAfterHeader);
                        System.out.println("[RESET BOT TOKEN] Retrying request in " + retryAfterSeconds / 1000 + " seconds");
                        Thread.sleep(retryAfterSeconds);
                        Thread.sleep(2000);
                        return resetBotTokenByAppId(applicationId);
                    }
                } else {
                    return null;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }


    // ~~won't use these methods~~
    public String generateANewPersonalApp(String name) throws IOException {
        // Create the request body
        JsonObject data = new JsonObject();
        data.addProperty("name", name);
        data.addProperty("bot_public", true);
        data.addProperty("bot_require_code_grant", false);
        data.addProperty("flags", 565248);
        if (code != null) {
            data.addProperty("code", TwoFactorAuth.generateCode(code));
        }
        RequestBody body = RequestBody.create(GSON.toJson(data), MediaType.get("application/json"));

        // Create the request
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Authorization", token)
                .build();

        // Send the request and get the response
        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // Request was successful
                var responseBody = response.body();
                if (responseBody == null) {
                    System.out.println("Response body is null");
                    return null;
                }
                JsonObject json = GSON.fromJson(responseBody.string(), JsonObject.class);
                return json.get("id").getAsString();
            } else {
                if (response.code() == 429) {
                    // Server returned a 429 response code, indicating that the client is being rate limited
                    String retryAfterHeader = response.headers().get("Retry-After");
                    if (retryAfterHeader != null) {
                        // Retry
                        // Retry-After header is present, parse the value to get the number of seconds to wait
                        long retryAfterSeconds = Long.parseLong(retryAfterHeader);
                        System.out.println("[GENERATE BOT] Retrying request in " + retryAfterSeconds / 1000 + " seconds");
                        Thread.sleep(retryAfterSeconds);
                        Thread.sleep(2000);
                        return generateANewPersonalApp(name);
                    }
                } else {
                    // Other non-successful response code, do something else here
                    return null;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }


    // ~~ won't use this method~~,
    // but it can be used, to generate a bot without getting the token
    public void buildANewBotWithoutGettingTheToken(String botId) throws IOException {
        // Create the request
        Request request = new Request.Builder()
                .url(API_URL + "/" + botId + "/bot")
                .post(RequestBody.create(MediaType.get("application/json"), "{}"))
                .addHeader("Authorization", token)
                .build();

        // Send the request and get the response
        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println("success, created bot: " + botId);
            } else {
                // Request was not successful
                if (response.code() == 429) {
                    // Server returned a 429 response code, indicating that the client is being rate limited
                    String retryAfterHeader = response.headers().get("Retry-After");
                    if (retryAfterHeader != null) {
                        // Retry-After header is present, parse the value to get the number of seconds to wait
                        long retryAfterSeconds = Long.parseLong(retryAfterHeader);
                        System.out.println("[SEND POST REQUEST] Retrying request in " + retryAfterSeconds / 1000 + " seconds");
                        Thread.sleep(retryAfterSeconds);
                        Thread.sleep(2000);
                    }
                } else {
                    // Other non-successful response code, do something else here
                    System.out.println("Request failed with response code " + response);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // ~~ won't use this method~~
    public List<Team> getTeams() throws IOException {
        String apiUrl = "https://discord.com/api/v9/teams";
        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", token)
                .build();

        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // Request was successful
                var responseBody = response.body();
                if (responseBody == null) {
                    System.out.println("Response body is null");
                    return null;
                }
                JsonArray jsonArray = GSON.fromJson(responseBody.string(), JsonArray.class);
                List<Team> teams = new ArrayList<>();
                jsonArray.forEach(element -> teams.add(GSON.fromJson(element, Team.class)));
                return teams;
            } else if (response.code() == 429) {
                // Request was rate limited
                String retryAfter = response.header("Retry-After");
                if(retryAfter == null){
                    retryAfter = "5000";
                }
                long retryAfterMillis = Long.parseLong(retryAfter) * 1000;
                System.out.println("[GET TEAMS] Rate limited, retrying in " + retryAfterMillis / 1000 + " milliseconds");
                Thread.sleep(retryAfterMillis);
                Thread.sleep(1000);
                return getTeams();
            } else {
                // Request was not successful
                System.out.println("INVALID TOKEN!");
                System.out.println("INVALID TOKEN!");
                System.exit(0);
                return Collections.emptyList();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }

    }

}