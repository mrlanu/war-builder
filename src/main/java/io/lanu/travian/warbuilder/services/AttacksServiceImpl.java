package io.lanu.travian.warbuilder.services;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.Cookie;
import io.lanu.travian.warbuilder.entities.WaveEntity;
import io.lanu.travian.warbuilder.models.AttackRequest;
import io.lanu.travian.warbuilder.repositories.WaveRepository;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
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

    private WaveRepository waveRepository;
    private WebClient webClient;
    private HttpClient httpClient;
    private String cookie;
    private HtmlPage pSPage;
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Autowired
    public AttacksServiceImpl(WaveRepository waveRepository, WebClient webClient,
                              HttpClient httpClient, ThreadPoolTaskScheduler threadPoolTaskScheduler) {
        this.waveRepository = waveRepository;
        this.webClient = webClient;
        this.httpClient = httpClient;
        this.threadPoolTaskScheduler = threadPoolTaskScheduler;
    }

    @Override
    public Integer[] getAvailableTroops(){

        if (isLoggedOut()){login();}

        Integer[] result = new Integer[11];

        try {
            pSPage = webClient.getPage(String.format("%s/build.php?tt=1&id=39",server));
        } catch (IOException e) {
            e.printStackTrace();
        }
        DomNodeList<DomNode> divs = pSPage.querySelectorAll("table");
        HtmlTable table = (HtmlTable) divs.get(0);
        HtmlTableBody body = table.getBodies().get(1);
        HtmlTableRow row = body.getRows().get(0);

        int i = 0;
        for (final HtmlTableCell cell : row.getCells()) {
            if (cell.getAttribute("class").contains("unit")){
                result[i] = Integer.parseInt(cell.asText());
                i++;
            }
        }
        logout();

        return result;
    }

    @Override
    public void scheduleAttack(List<AttackRequest> attackRequest){

        if (isLoggedOut()){login();}

        Date sendingTime = getPerfectTime(attackRequest);

        if (sendingTime.after(new Date())){
            createTask(sendingTime, attackRequest);
            System.out.println("Attack has been scheduled at - " + sendingTime);
        }else {
            System.out.println("Attack can't be scheduled. Not enough time.");
        }
        logout();
    }

    private Date getPerfectTime(List<AttackRequest> attackRequest){
        LocalDateTime serverTime = LocalDateTime.now(ZoneId.of("Europe/Moscow")).truncatedTo(ChronoUnit.SECONDS);
        System.out.println("Server time - " + serverTime);

        LocalDateTime attackRequestTime = attackRequest.get(0).getTime();
        System.out.println("Requested attack time - " + attackRequestTime);

        long timeForAttack = 0;
        try {
            timeForAttack = addWave(attackRequest.get(0), true);
            System.out.println("Time needed for Attack - " +
                    DurationFormatUtils.formatDuration(timeForAttack, "HH:mm:ss"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        long availableTime = Duration.between(serverTime, attackRequestTime).toMillis();
        System.out.println("Available time - " +
                DurationFormatUtils.formatDuration(availableTime, "HH:mm:ss"));

        LocalDateTime perfectTime = serverTime.plus(availableTime, ChronoUnit.MILLIS)
                .minus(timeForAttack, ChronoUnit.MILLIS)
                .minus(10000, ChronoUnit.MILLIS)
                .minus(9, ChronoUnit.HOURS);

        return Date.from(perfectTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private long addWave(AttackRequest attackRequest, boolean isEstimating) throws IOException {
        long result;
        String requestForAttack = createRequestForAttack(attackRequest);
        String responseForAttack = sendAsyncRequest(requestForAttack, true);

        result = getTimeForAttack(responseForAttack);

        if (!isEstimating){
            addAttackRequestToQueue(attackRequest, responseForAttack, result);
        }

        return result;
    }

    private String createRequestForAttack(AttackRequest attackRequest) throws IOException {
        List<HtmlTextInput> inputTroopsList = new ArrayList<>();
        //setup initial values for attack
        HtmlForm attackForm = pSPage.getFormByName("snd");
        HtmlTextInput textFieldX = attackForm.getInputByName("x");
        HtmlTextInput textFieldY = attackForm.getInputByName("y");
        textFieldX.reset();
        textFieldY.reset();
        textFieldX.type(attackRequest.getX().toString());
        textFieldY.type(attackRequest.getY().toString());

        for (int i=1; i <= 11; i++){
            List<HtmlInput> inpList = attackForm.getInputsByName(String.format("troops[0][t%d]", i));
            if (inpList.size() != 0){
                HtmlTextInput inp = (HtmlTextInput) inpList.get(0);
                inputTroopsList.add(inp);
                inputTroopsList.get(inputTroopsList.size() - 1).reset();
                inputTroopsList.get(inputTroopsList.size() - 1).type(attackRequest.getTroops()[i - 1].toString());
            }
        }

        List<HtmlInput> radio = attackForm.getInputsByName("c");
        radio.stream().filter(i -> i.getValueAttribute().equals(attackRequest.getKindAttack().toString()))
                .findFirst().get().setChecked(true);

        List<HtmlElement> allInputs = attackForm.getElementsByTagName("input");

        return allInputs.stream()
                .map(i -> (HtmlInput) i)
                .filter(i -> !i.getNameAttribute().equals("redeployHero"))
                .filter(i -> !(i.getNameAttribute().equals("c") && !i.isChecked()))
                .map(i -> i.getNameAttribute() + "=" + i.getValueAttribute())
                .collect(Collectors.joining("&"));
    }

    private long getTimeForAttack(String attackResponse){
        Document doc = Jsoup.parse(attackResponse);
        Element timeEl = doc.select(".in").first();
        String time = timeEl.wholeText().split(" ")[1];
        String[] timeArr = time.split(":");
        if (timeArr[0].length() == 1){
            timeArr[0] = "0" + timeArr[0];
            time = timeArr[0] + ":" + timeArr[1] + ":" + timeArr[2];
        }
        LocalTime timeToAttack = LocalTime.parse(time);

        return timeToAttack.getLong(ChronoField.MILLI_OF_DAY);
    }

    private void createAttack(List<AttackRequest> attackRequest) {
            attackRequest.forEach(a -> {
                try {
                    addWave(a, false);
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
    }

    private void confirmAttack(String attackId) {
        waveRepository.findAllByAttackId(attackId).forEach(waveEntity -> {
            sendAsyncRequest(waveEntity.getAttackRequest(), false);
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println("Attack has been sent.");
        waveRepository.deleteAllByAttackId(attackId);
    }

    private void addAttackRequestToQueue(AttackRequest attackRequest, String attackResponse, long timeForAttack){
        Document doc = Jsoup.parse(attackResponse);
        List<Element> link = doc.select("input");
        Element button = doc.select("#btn_ok").first();

        String parsedResult = link
                .stream()
                .map(l -> l.attr("name") + "=" + l.attr("value"))
                .collect(Collectors.joining("&", "", "&" + button.attr("name") + "=" + button.attr("value")));

        if (attackRequest.getTroops()[7] >= 20){
            parsedResult += "&troops[0][kata]=" + attackRequest.getFirstTarget();
            parsedResult += "&troops[0][kata2]=" + attackRequest.getSecondTarget();
        }else if (attackRequest.getTroops()[7] >= 1){
            parsedResult += "&troops[0][kata]=" + attackRequest.getFirstTarget();
        }

        WaveEntity waveEntity = new WaveEntity(attackRequest.getAttackId(), timeForAttack, parsedResult);

        //System.out.println("Parsed result - " + parsedResult);

        waveRepository.save(waveEntity);
    }

    private String sendAsyncRequest(String request, boolean needResponseBody){
        HttpRequest req = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(request))
                .uri(URI.create(String.format("%s/build.php?tt=2&id=39",server)))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
                .header("user-agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36")
                .header("origin", "https://ts2.travian.ru")
                .header("Cookie", cookie)
                .build();

        try {
            CompletableFuture<HttpResponse<String>> response = httpClient.sendAsync(req, HttpResponse.BodyHandlers.ofString());

            if (needResponseBody){
                return response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);
            }else return null;

        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void createTask(Date sendingTime, List<AttackRequest> attackRequest){
        threadPoolTaskScheduler.schedule(() -> {

            if (isLoggedOut()){login();}

            createAttack(attackRequest);
            String attackId = attackRequest.get(0).getAttackId();
            long timeForAttack = waveRepository.findAllByAttackId(attackId).get(0).getTimeForAttack();
            LocalDateTime serverTime = LocalDateTime.now(ZoneId.of("Europe/Moscow")).truncatedTo(ChronoUnit.SECONDS);
            LocalDateTime attackRequestTime = attackRequest.get(0).getTime();
            long availableTime = Duration.between(serverTime, attackRequestTime).toMillis() - timeForAttack;

            try {
                TimeUnit.MILLISECONDS.sleep(availableTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            confirmAttack(attackId);

            logout();
            System.out.println("<<<<-----All done----->>>>");

        }, sendingTime);
    }

    private void login(){
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

    private void logout(){
        HtmlAnchor logoutA = pSPage.getAnchorByHref("logout.php");
        try {
            pSPage = logoutA.click();
            System.out.println("Logged Out");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isLoggedOut(){
        if (pSPage == null){return true;}
        String heroName = "";
        List<HtmlElement> elements = pSPage.getByXPath("//div[@class='playerName']//a[@href='spieler.php']");
        if (elements.size() > 0){
            HtmlAnchor htmlAnchorHeroName = (HtmlAnchor) elements.get(1);
            heroName = htmlAnchorHeroName.asText();
        }
        return !heroName.equals(userName);
    }
}
