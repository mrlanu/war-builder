package io.lanu.travian.warbuilder;

import io.lanu.travian.warbuilder.services.Player;
import io.lanu.travian.warbuilder.services.SharedService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@SpringBootApplication
@EnableScheduling
public class WarBuilderApplication implements CommandLineRunner {

    private Player player;
    private SharedService sharedService;

    public WarBuilderApplication(Player player, SharedService sharedService) {
        this.player = player;
        this.sharedService = sharedService;
    }

    public static void main(String[] args) {
        SpringApplication.run(WarBuilderApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        login();
    }

    private void login() throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))){

            System.out.println("--->>>  Please enter a Client Id:");
            String playerId = reader.readLine();
            player.setClientId(playerId);

            System.out.println("Client Id - " + playerId + " saved");

            while (true) {
                System.out.println("--->>>  Please enter a Travian username:");
                String userName = reader.readLine();

                if (userName.equals("fuck off")){
                    userName = "Баба Яга";
                }

                player.setTravianUserName(userName);


                System.out.println("--->>>  Please enter Travian password:");
                player.setTravianPass(reader.readLine());

                System.out.println("Trying to login.");

                if (sharedService.login()){
                    System.out.println("Ready to GO !");
                    break;
                }
            }
        }
    }
}
