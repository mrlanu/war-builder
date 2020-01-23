package io.lanu.travian.warbuilder.services;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
    private Player player;

    public SharedServiceImpl(WebClient webClient, Player player) {
        this.webClient = webClient;
        this.player = player;
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
    public boolean login(){
        String heroName = null;
        try {
            pSPage = webClient.getPage(server + "/dorf1.php");
            HtmlForm loginForm = pSPage.getFormByName("login");
            HtmlButton button = loginForm.getButtonByName("s1");
            HtmlTextInput textField = loginForm.getInputByName("name");
            HtmlCheckBoxInput checkBoxInput = loginForm.getInputByName("lowRes");
            checkBoxInput.setChecked(true);
            HtmlPasswordInput textFieldPass = loginForm.getInputByName("password");
            textField.type(player.getTravianUserName());
            textFieldPass.type(player.getTravianPass());

            //Village Page
            pSPage = button.click();

            //get Hero name
            List<HtmlDivision> divisions = pSPage.getByXPath("//div[@class='playerName']");

            if (divisions.size() < 1){
                System.out.println("Login Failed. Try again.");
                return false;
            }

            heroName = divisions.get(0).getTextContent();

            //get cookie
            Set<Cookie> cookieSet = webClient.getCookies(pSPage.getUrl());
            StringBuilder cB = new StringBuilder();
            cookieSet.stream().filter(c -> c.toString().startsWith("J")).forEach(cB::append);
            cookie = cB.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Successfully logged in. Welcome - " + heroName);
        return true;
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
        List<HtmlElement> elements = pSPage.getByXPath("//div[@class='playerName']");
        if (elements.size() > 0){
            HtmlDivision htmlDivHeroName = (HtmlDivision) elements.get(0);
            heroName = htmlDivHeroName.getTextContent();
        }
        return !heroName.equals(player.getTravianUserName());
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
