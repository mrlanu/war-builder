package io.lanu.travian.warbuilder.services;

import com.gargoylesoftware.htmlunit.html.*;
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

        //sharedService.logout();

        return villages;
    }

    @Override
    public Integer[] getAvailableTroops(){

        HtmlPage currentPage;

        if (sharedService.isLoggedOut()){sharedService.login();}

        Integer[] result = new Integer[11];

        currentPage = sharedService.getPage("build.php?tt=1&id=39");
        List<HtmlElement> nodes = currentPage.getByXPath("//table[@class='troop_details']");
        HtmlTable table = (HtmlTable) nodes.get(0);
        HtmlTableBody body = table.getBodies().get(1);
        HtmlTableRow row = body.getRows().get(0);

        int i = 0;
        for (final HtmlTableCell cell : row.getCells()) {
            if (cell.getAttribute("class").contains("unit")){
                result[i] = Integer.parseInt(cell.asText());
                i++;
            }
        }
        //sharedService.logout();

        return result;
    }
}
