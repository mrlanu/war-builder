package io.lanu.travian.warbuilder.services;

import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class BuildServiceImpl implements BuildService {

    private SharedService sharedService;
    private InformationService informationService;

    public BuildServiceImpl(SharedService sharedService, InformationService informationService) {
        this.sharedService = sharedService;
        this.informationService = informationService;
    }

    @Scheduled(fixedDelay = 300000, initialDelay = 30000)
    public void test() throws IOException {

        List<Material> materials = Arrays.asList(
                new Material("wood", 0, "gid1"),
                new Material("clay", 0, "gid2"),
                new Material("iron", 0, "gid3"),
                new Material("crop", 0, "gid4")
        );

        sleepRandom();

        if (sharedService.isLoggedOut()){sharedService.login();}
        informationService.changeActiveVillage("16pO5dZa78eBa7Lo");

        HtmlPage page;
        page = sharedService.getPage("/dorf1.php");

        materials.get(0).amount = Integer.parseInt(page.getHtmlElementById("l1").getTextContent()
                .replaceAll("[\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}\\s]", ""));
        materials.get(1).amount = Integer.parseInt(page.getHtmlElementById("l2").getTextContent()
                .replaceAll("[\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}\\s]", ""));
        materials.get(2).amount = Integer.parseInt(page.getHtmlElementById("l3").getTextContent()
                .replaceAll("[\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}\\s]", ""));
        materials.get(3).amount = Integer.parseInt(page.getHtmlElementById("l4").getTextContent()
                .replaceAll("[\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}\\s]", ""));

        HtmlElement cont = page.getHtmlElementById("resourceFieldContainer");

        Material minMaterial = materials
                .stream()
                .min(Comparator.comparingInt(a -> a.amount))
                .get();

        System.out.println(minMaterial.name + " - " + minMaterial.amount);

        List<HtmlDivision> divisions = cont.getElementsByTagName("div")
                .stream()
                .map(e -> (HtmlDivision) e)
                .filter(e -> e.getAttribute("class").contains(minMaterial.label) &&
                        e.getAttribute("class").contains("good"))
                .collect(Collectors.toList());

        System.out.println(divisions.get(0).getAttribute("onclick"));

        String path = divisions.get(0).getAttribute("onclick").split("'")[1];

        page = sharedService.getPage(path);

        HtmlDivision buttonDiv = (HtmlDivision) page.getByXPath("//div[@class='section1']").get(0);
        HtmlButton button = (HtmlButton) buttonDiv.getElementsByTagName("button").get(0);

        path = button.getAttribute("onclick").split("'")[1];

        page = sharedService.getPage(path);

        System.out.println(page.getUrl().getPath());
        System.out.println("The end");
    }

    private void sleepRandom(){
        Random r = new Random();
        int low = 1;
        int high = 10;
        int result = r.nextInt(high-low) + low;
        System.out.println(String.format("Random sleep going to be %d seconds", result));
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
