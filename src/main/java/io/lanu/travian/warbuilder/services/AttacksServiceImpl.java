package io.lanu.travian.warbuilder.services;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.net.http.HttpClient;
import java.util.Set;

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
}
