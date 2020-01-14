package io.lanu.travian.warbuilder.services;

import com.gargoylesoftware.htmlunit.html.*;
import io.lanu.travian.warbuilder.entities.WaveEntity;
import io.lanu.travian.warbuilder.models.AttackRequest;
import io.lanu.travian.warbuilder.models.WaveModel;
import io.lanu.travian.warbuilder.repositories.WaveRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
public class AttacksServiceImpl implements AttacksService{

    private WaveRepository waveRepository;
    private HttpClient httpClient;
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    private SharedService sharedService;
    private InformationService informationService;

    @Autowired
    public AttacksServiceImpl(WaveRepository waveRepository, HttpClient httpClient,
                              ThreadPoolTaskScheduler threadPoolTaskScheduler, SharedService sharedService,
                              InformationService informationService) {
        this.waveRepository = waveRepository;
        this.httpClient = httpClient;
        this.threadPoolTaskScheduler = threadPoolTaskScheduler;
        this.sharedService = sharedService;
        this.informationService = informationService;
    }

    @Override
    public void sendSpam(AttackRequest attackRequest){
        getVillagesForSpam().forEach(village -> {
            attackRequest.setX(village.getX());
            attackRequest.setY(village.getY());
            scheduleAttack(attackRequest);
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

    }

    @Override
    public void scheduleAttack(AttackRequest attackRequest){

        if (sharedService.isLoggedOut()){sharedService.login();}
        changeActiveVillage(attackRequest.getVillageName());

        if (attackRequest.isImmediately()){
            createAttack(attackRequest);
            confirmAttack(attackRequest.getAttackId());
            return;
        }

        Date sendingTime = getPerfectTime(attackRequest);

        if (sendingTime == null){
            return;
        }

        if (sendingTime.after(new Date())){
            createTask(attackRequest.getVillageName(), sendingTime, attackRequest);
            System.out.println("Attack has been scheduled at - " + sendingTime);
        }else {
            System.out.println("Attack can't be scheduled. Not enough time.");
        }
        //sharedService.logout();
    }

    private Date getPerfectTime(AttackRequest attackRequest){
        LocalDateTime serverTime = LocalDateTime.now(ZoneId.of("Europe/Moscow")).truncatedTo(ChronoUnit.SECONDS);
        System.out.println("Server time - " + serverTime);

        LocalDateTime attackRequestTime = new java.sql.Timestamp(
                attackRequest.getTime().getTime()).toLocalDateTime().plusHours(6);
        System.out.println("Requested attack time - " + attackRequestTime);

        if (serverTime.isAfter(attackRequestTime)){
            System.out.println("[x] Attack hasn't been scheduled. Wrong time requested.");
            return null;
        }
        long timeForAttack = 0;
        try {
            timeForAttack = addWave(attackRequest, 0, true);
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

    private long addWave(AttackRequest attackRequest, int waveNumber, boolean isEstimating) throws IOException {
        long result;

        String requestForAttack = createRequestForAttack(attackRequest, waveNumber);
        String presetAttack = sendAsyncRequest(requestForAttack, true);

        result = getTimeForAttack(presetAttack);

        if (!isEstimating){
            addAttackRequestToQueue(attackRequest, waveNumber, presetAttack, result);
        }

        return result;
    }

    private String createRequestForAttack(AttackRequest attackRequest, int waveNumber) throws IOException {
        List<HtmlTextInput> inputTroopsList = new ArrayList<>();
        //setup initial values for attack
        HtmlPage currentPage = sharedService.getpSPage();
        HtmlForm attackForm = currentPage.getFormByName("snd");
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
                inputTroopsList.get(inputTroopsList.size() - 1)
                        .type(attackRequest.getWaves().get(waveNumber).getTroops()[i - 1].toString());
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

    private void createAttack(AttackRequest attackRequest) {
        for (int i=0; i < attackRequest.getWaves().size(); i++){
            try {
                addWave(attackRequest, i, false);
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
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

    private void addAttackRequestToQueue(AttackRequest attackRequest, int waveNumber, String presetAttack, long timeForAttack){
        Document doc = Jsoup.parse(presetAttack);
        List<Element> link = doc.select("input");
        Element button = doc.select("#btn_ok").first();

        String parsedResult = link
                .stream()
                .map(l -> l.attr("name") + "=" + l.attr("value"))
                .collect(Collectors.joining("&", "", "&" + button.attr("name") + "=" + button.attr("value")));

        WaveModel waveConfig = attackRequest.getWaves().get(waveNumber);
        if (waveConfig.getTroops()[7] >= 20){
            parsedResult += "&troops[0][kata]=" + waveConfig.getFirstTarget();
            parsedResult += "&troops[0][kata2]=" + waveConfig.getSecondTarget();
        }else if (waveConfig.getTroops()[7] >= 1){
            parsedResult += "&troops[0][kata]=" + waveConfig.getFirstTarget();
        }

        WaveEntity waveEntity = new WaveEntity(attackRequest.getAttackId(), timeForAttack, parsedResult);

        //System.out.println("Parsed result - " + parsedResult);

        waveRepository.save(waveEntity);
    }

    private String sendAsyncRequest(String request, boolean needResponseBody){
        HttpRequest req = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(request))
                .uri(URI.create(String.format("%s/build.php?tt=2&id=39", sharedService.getServer())))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
                .header("user-agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36")
                .header("origin", "https://ts2.travian.ru")
                .header("Cookie", sharedService.getCookie())
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

    private void createTask(String attackingVillageName, Date sendingTime, AttackRequest attackRequest){
        threadPoolTaskScheduler.schedule(() -> {

            if (sharedService.isLoggedOut()){sharedService.login();}

            changeActiveVillage(attackingVillageName);

            createAttack(attackRequest);
            String attackId = attackRequest.getAttackId();
            long timeForAttack = waveRepository.findAllByAttackId(attackId).get(0).getTimeForAttack();
            LocalDateTime serverTime = LocalDateTime.now(ZoneId.of("Europe/Moscow")).truncatedTo(ChronoUnit.SECONDS);
            LocalDateTime attackRequestTime = new java.sql.Timestamp(
                    attackRequest.getTime().getTime()).toLocalDateTime().plusHours(6);
            long availableTime = Duration.between(serverTime, attackRequestTime).toMillis()
                    - timeForAttack - attackRequest.getTimeCorrection();

            try {
                TimeUnit.MILLISECONDS.sleep(availableTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            confirmAttack(attackId);

            //sharedService.logout();
            System.out.println("<<<<-----All done----->>>>");

        }, sendingTime);
    }

    private void changeActiveVillage(String attackingVillageName){
        String id = informationService.getAllVillages().stream()
                .filter(villageModel -> villageModel.getName().equals(attackingVillageName))
                .findFirst().get().getId();
        sharedService.getPage("dorf2.php" + id);
        sharedService.getPage("build.php?tt=2&id=39");
    }

    private List<Coordinates> getVillagesForSpam(){
        final List<Coordinates> result = new ArrayList<>();
        if (sharedService.isLoggedOut()){sharedService.login();}

        //find envelope link
        List<HtmlAnchor> anchors = sharedService.getpSPage().getByXPath("//a[@href='messages.php']");
        try {
            //go to messages page
            HtmlPage page = anchors.get(0).click();

            //get last message with text 'spam or Spam'
            List<HtmlAnchor> anchorList =
                    page.getByXPath("//img[contains(@class, 'messageStatus')]//parent::a[1]" +
                            "//following-sibling::a[contains(text(),'спам') or contains(text(),'Спам')]");
            //go inside that message
            page = anchorList.get(0).click();
            //get all villages from message
            anchorList = page.getByXPath("//a[@class='bbCoordsLink']");

            anchorList.forEach(htmlAnchor -> {
                String[] textArr = htmlAnchor.getTextContent().split(" ");
                String coords = textArr[textArr.length - 1]
                        .replaceAll("[\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}]", "")
                        .replaceAll("\u2212", "-");
                String[] coordArr = coords.split("[(,|,)]");
                result.add(new Coordinates(Integer.parseInt(coordArr[1]),
                        Integer.parseInt(coordArr[2])));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Data
    @AllArgsConstructor
    public static class Coordinates{
        private int x;
        private int y;
    }
}
