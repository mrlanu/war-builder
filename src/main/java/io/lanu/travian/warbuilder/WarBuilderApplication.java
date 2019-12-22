package io.lanu.travian.warbuilder;

import com.gargoylesoftware.htmlunit.WebClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.net.http.HttpClient;

@SpringBootApplication
public class WarBuilderApplication {

    public static void main(String[] args) {
        SpringApplication.run(WarBuilderApplication.class, args);
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
