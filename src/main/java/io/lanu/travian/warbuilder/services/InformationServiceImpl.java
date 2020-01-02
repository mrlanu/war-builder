package io.lanu.travian.warbuilder.services;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InformationServiceImpl implements InformationService {

    private SharedService sharedService;

    public InformationServiceImpl(SharedService sharedService) {
        this.sharedService = sharedService;
    }

    @Override
    public Map<String, String> getAllVillages(){

        Map<String, String> villages = new HashMap<>();

        if (sharedService.isLoggedOut()){sharedService.login();}

        List<HtmlAnchor> anchors = sharedService.getpSPage()
                .getByXPath("//div[@id='sidebarBoxVillagelist']//div[@class='innerBox content']//a");

        anchors.forEach(htmlAnchor -> {
            HtmlDivision d = (HtmlDivision) htmlAnchor.getElementsByAttribute("div", "class", "name").get(0);
            villages.put(d.getTextContent(), htmlAnchor.getHrefAttribute());
        });

        sharedService.logout();

        return villages;
    }
}
