package io.lanu.travian.warbuilder.services;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Set;

@Service
public class SharedServiceImpl implements SharedService {

    @Value("${travian.server}")
    private String server;

    @Value("${travian.user.name}")
    private String userName;

    @Value("${travian.user.password}")
    private String password;

    private WebClient webClient;
    private String cookie;
    private HtmlPage pSPage;

    public SharedServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public HtmlPage getPage(String url){
        try {
            pSPage = webClient.getPage(String.format("%s/%s", server, url));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pSPage;
    }

    @Override
    public void login(){
        String heroName = null;
        try {
            pSPage = webClient.getPage(server + "/dorf1.php");
            HtmlForm loginForm = pSPage.getFormByName("login");
            HtmlButton button = loginForm.getButtonByName("s1");
            HtmlTextInput textField = loginForm.getInputByName("name");
            HtmlCheckBoxInput checkBoxInput = loginForm.getInputByName("lowRes");
            checkBoxInput.setChecked(true);
            HtmlPasswordInput textFieldPass = loginForm.getInputByName("password");
            textField.type(userName);
            textFieldPass.type(password);

            //Village Page
            pSPage = button.click();
            HtmlAnchor htmlAnchorHeroName = (HtmlAnchor) pSPage.getByXPath("//div[@class='playerName']//a[@href='spieler.php']").get(1);
            heroName = htmlAnchorHeroName.asText();
            URL url = new URL(String.format("%s/build.php?tt=2&id=39",server));

            //get cookie
            Set<Cookie> cookieSet = webClient.getCookies(url);
            StringBuilder cB = new StringBuilder();
            cookieSet.stream().filter(c -> c.toString().startsWith("J")).forEach(cB::append);
            cookie = cB.toString();
            pSPage = webClient.getPage(String.format("%s/build.php?tt=2&id=39",server));
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Successfully logged in. Welcome - " + heroName);
    }

    @Override
    public void logout(){
        HtmlAnchor logoutA = pSPage.getAnchorByHref("logout.php");
        try {
            pSPage = logoutA.click();
            System.out.println("Logged Out");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isLoggedOut(){
        if (pSPage == null){return true;}
        String heroName = "";
        List<HtmlElement> elements = pSPage.getByXPath("//div[@class='playerName']//a[@href='spieler.php']");
        if (elements.size() > 0){
            HtmlAnchor htmlAnchorHeroName = (HtmlAnchor) elements.get(1);
            heroName = htmlAnchorHeroName.asText();
        }
        return !heroName.equals(userName);
    }

    public String getCookie() {
        return cookie;
    }

    public HtmlPage getpSPage() {
        return pSPage;
    }

    public String getServer() {
        return server;
    }
}
