package dev.wee;

import com.google.gson.Gson;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Config {
     String selfToken;
     String secret;
     String channelId;
     String standardName;
     String botTokenToSendInfo;

    public Config(String selfToken, String secret, String channel, String standardName, String botToken) {
        this.selfToken = selfToken;
        this.secret = secret;
        this.channelId = channel;
        this.standardName = standardName;
        this.botTokenToSendInfo = botToken;
    }

    public static Config readConfig() throws IOException {
        // check if config exits
        File file = new File("config.json");
        Gson gson = new Gson();
        if (!file.exists()) {
            System.out.println("Config file not found!");
            file.createNewFile();
            System.out.println("Created config file!");
            FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8);
            writer.write(gson.toJson(new Config("", "", "", "", "")));
            writer.close();
                    System.out.println("Please fill in the config file and restart the program!");
            System.exit(0);
        }
        return gson.fromJson(new String(Files.readAllBytes(file.toPath())), Config.class);
    }
}
