package io.lanu.travian.warbuilder;

import com.gargoylesoftware.htmlunit.WebClient;
import io.lanu.travian.warbuilder.services.Player;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.http.HttpClient;

@SpringBootApplication
public class WarBuilderApplication implements CommandLineRunner {

    private Player player;

    public WarBuilderApplication(Player player) {
        this.player = player;
    }

    public static void main(String[] args) {
        SpringApplication.run(WarBuilderApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))){
            System.out.println("Please enter a Player Id:");
            String playerId = reader.readLine();
            player.setPlayerId(playerId);
            System.out.println("Player Id - " + playerId + " saved");
        }
    }

    @Bean
    public WebClient getWebClient(){
        WebClient webClient = new WebClient();
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setDownloadImages(false);
        webClient.getOptions().setJavaScriptEnabled(false);
        return webClient;
    }

    @Bean
    public HttpClient getHttpClient(){
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();
    }

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler(){
        ThreadPoolTaskScheduler threadPoolTaskScheduler
                = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(5);
        threadPoolTaskScheduler.setThreadNamePrefix(
                "ThreadPoolTaskScheduler");
        return threadPoolTaskScheduler;
    }
}
