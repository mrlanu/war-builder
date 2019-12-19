package io.lanu.travian.warbuilder.services;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.Cookie;
import io.lanu.travian.warbuilder.models.AttackRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
public class AttacksServiceImpl implements AttacksService{

    @Value("${travian.server}")
    private String server;

    @Value("${travian.user.name}")
    private String userName;

    @Value("${travian.user.password}")
    private String password;

    private WebClient webClient;
    private HttpClient httpClient;
    private String cookie;

    @Autowired
    public AttacksServiceImpl(WebClient webClient, HttpClient httpClient) {
        this.webClient = webClient;
        this.httpClient = httpClient;
    }

    @Override
    public String login(){
        String heroName = null;
        try {
            HtmlPage startPage = webClient.getPage(server + "/dorf1.php");
            HtmlForm loginForm = startPage.getFormByName("login");
            HtmlButton button = loginForm.getButtonByName("s1");
            HtmlTextInput textField = loginForm.getInputByName("name");
            HtmlCheckBoxInput checkBoxInput = loginForm.getInputByName("lowRes");
            checkBoxInput.setChecked(true);
            HtmlPasswordInput textFieldPass = loginForm.getInputByName("password");
            textField.type(userName);
            textFieldPass.type(password);

            //Village Page
            HtmlPage currentPage = button.click();
            HtmlAnchor htmlAnchorHeroName = (HtmlAnchor) currentPage.getByXPath("//div[@class='playerName']//a[@href='spieler.php']").get(1);
            heroName = htmlAnchorHeroName.asText();
            URL url = new URL(String.format("%s/build.php?tt=2&id=39",server));

            //get cookie
            Set<Cookie> cookieSet = webClient.getCookies(url);
            StringBuilder cB = new StringBuilder();
            cookieSet.forEach(cookie -> cB.append(cookie));
            cookie = cB.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return heroName;
    }

    @Override
    public void createAttack(List<AttackRequest> attackRequest) {

    }

    public void addWave(String troops, String x, String y) throws IOException {

        //setup initial values for attack
        HtmlPage pSPage = webClient.getPage(String.format("%s/build.php?tt=2&id=39",server));
        HtmlForm attackForm = pSPage.getFormByName("snd");
        HtmlTextInput textField = attackForm.getInputByName("troops[0][t5]");
        HtmlTextInput textFieldX = attackForm.getInputByName("x");
        HtmlTextInput textFieldY = attackForm.getInputByName("y");
        HtmlRadioButtonInput radio = attackForm.getInputByName("c");
        radio.setDefaultValue("3");
        textFieldX.type(x);
        textFieldY.type(y);
        textField.type(troops);

        List<HtmlElement> inputs = attackForm.getElementsByTagName("input");
        //create parameters for request

        //join everything
        String attackRequest = inputs.stream()
                .map(i -> (HtmlInput) i)
                .filter(i -> !i.isChecked())
                .map(i -> i.getNameAttribute() + "=" + i.getValueAttribute())
                .collect(Collectors.joining("&"));

        System.out.println("Result is - " + attackRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(attackRequest))
                .uri(URI.create(String.format("%s/build.php?tt=2&id=39",server)))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
                .header("Cookie", cookie)
                .build();

        try {
            CompletableFuture<HttpResponse<String>> response = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Attack Request has been sent.");
            String body = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);

            addAttackRequestToQueue(body);
            System.out.println("----------------------");
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    private void addAttackRequestToQueue(String attackResponse){
        Document doc = Jsoup.parse(attackResponse);
        List<Element> link = doc.select("input");
        Element button = doc.select("#btn_ok").first();

        String parsedResult = link
                .stream()
                .map(l -> l.attr("name") + "=" + l.attr("value"))
                .collect(Collectors.joining("&", "", button.attr("name") + "=" + button.attr("value")));

        //attacksList.add(sParams.toString());
    }
}
