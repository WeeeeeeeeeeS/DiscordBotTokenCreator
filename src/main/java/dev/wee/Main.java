package dev.wee;

import java.io.IOException;
import java.util.Scanner;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;


public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("*************************************");
        System.out.println("Now loading config...");
        Config c = Config.readConfig();
        Scanner scanner = new Scanner(System.in);
        JDA jda;
        try {
            jda = JDABuilder.createDefault(c.botTokenToSendInfo).build().awaitReady();
        } catch (Exception e) {
            System.out.println("Invalid BOT token!");
            return;
        }
        TextChannel channel = jda.getTextChannelById(c.channelId);
        if (channel == null) {
            System.out.println("Invalid channel ID!");
            return;
        }

        System.out.println("Config loaded!~~");
        System.out.println("*************************************");
        System.out.println("Welcome to the Discord Bot's Token Creator");
        PersonalDevAccount account = new PersonalDevAccount(c.selfToken, c.secret, channel);
        System.out.print("Enter the number of bots to create: ");
        int howManyBots;
        while (true) {
            try {
                howManyBots = Integer.parseInt(scanner.nextLine());
                break;
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
        System.out.println("************************************");
        System.out.println("Creating " + howManyBots + " bots...");
        System.out.println("It may take a while..., so please wait!");
        System.out.println("************************************");
        try {
            account.generateBots(c.standardName, howManyBots);
        } catch (IOException e) {
            System.out.println("Error while creating bots!: " + e.getMessage());
        }

        channel.sendMessage("Finished creating " + howManyBots + " bots!").queue();
        System.out.println("************************************");
    }

}