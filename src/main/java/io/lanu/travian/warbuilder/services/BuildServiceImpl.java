package io.lanu.travian.warbuilder.services;

import com.gargoylesoftware.htmlunit.html.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BuildServiceImpl implements BuildService {

    private SharedService sharedService;
    private InformationService informationService;

    private static Integer timer = 0;

    public BuildServiceImpl(SharedService sharedService, InformationService informationService) {
        this.sharedService = sharedService;
        this.informationService = informationService;
    }

    @Scheduled(fixedDelay = 600000, initialDelay = 30000)
    public void upgradeSomething() throws IOException {

        timer -= 600;
        if (timer > 0) {
            log.info("Current timer: " + timer);
            return;
        }

        List<Material> materials = Arrays.asList(
                new Material("wood", 0, "gid1"),
                new Material("clay", 0, "gid2"),
                new Material("iron", 0, "gid3"),
                new Material("crop", 0, "gid4")
        );

        sleepRandom();

        if (sharedService.isLoggedOut()){sharedService.login();}
        informationService.changeActiveVillage("16pO5dZa78eBa7Lo");

        HtmlPage page = sharedService.getPage("/dorf1.php");

        materials.get(0).amount = Integer.parseInt(page.getHtmlElementById("l1").getTextContent()
                .replaceAll("[\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}\\s]", ""));
        materials.get(1).amount = Integer.parseInt(page.getHtmlElementById("l2").getTextContent()
                .replaceAll("[\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}\\s]", ""));
        materials.get(2).amount = Integer.parseInt(page.getHtmlElementById("l3").getTextContent()
                .replaceAll("[\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}\\s]", ""));
        materials.get(3).amount = Integer.parseInt(page.getHtmlElementById("l4").getTextContent()
                .replaceAll("[\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}\\s]", ""));

        Material minMaterial = materials
                .stream()
                .min(Comparator.comparingInt(a -> a.amount))
                .orElse(materials.get(0));

        HtmlElement fieldsContainer = page.getHtmlElementById("resourceFieldContainer");

        List<HtmlDivision> divsGoodForUpgrade = fieldsContainer.getElementsByTagName("div")
                .stream()
                .map(e -> (HtmlDivision) e)
                .filter(e -> e.getAttribute("class").contains("good"))
                .collect(Collectors.toList());

        if (divsGoodForUpgrade.size() > 0){
            HtmlDivision divForUpgrade = divsGoodForUpgrade
                    .stream()
                    .filter(d -> d.getAttribute("class").contains(minMaterial.label))
                    .min((a, b) -> {
                        HtmlDivision divA = a.getFirstByXPath("div[@class='labelLayer']");
                        HtmlDivision divB = b.getFirstByXPath("div[@class='labelLayer']");
                        return Integer.parseInt(divA.getTextContent()) - Integer.parseInt(divB.getTextContent());
                    })
                    .orElseGet(() -> divsGoodForUpgrade.get(0));

            confirmUpgrade(divForUpgrade);

            page = sharedService.getpSPage();
            HtmlSpan timerSpan = (HtmlSpan) page.getByXPath("//span[@class='timer']").get(0);
            timer = Integer.parseInt(timerSpan.getAttribute("value"));

            log.info("Upgrade has been set");
        } else {
            log.info("Nothing available for upgrade");
        }
    }

    private void confirmUpgrade(HtmlDivision division){

        HtmlPage page;

        String path = division.getAttribute("onclick").split("'")[1];
        page = sharedService.getPage(path);

        HtmlDivision buttonContainer = (HtmlDivision) page.getByXPath("//div[@class='section1']").get(0);
        HtmlButton button = (HtmlButton) buttonContainer.getElementsByTagName("button").get(0);

        path = button.getAttribute("onclick").split("'")[1];
        sharedService.getPage(path);
    }

    private void sleepRandom(){
        Random r = new Random();
        int low = 1;
        int high = 10;
        int result = r.nextInt(high-low) + low;
        log.info(String.format("Random sleep going to be %d seconds", result));
        try {
            TimeUnit.SECONDS.sleep(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class Material{
        String name;
        int amount;
        String label;

        public Material(String name, int amount, String label) {
            this.name = name;
            this.amount = amount;
            this.label = label;
        }
    }
}
