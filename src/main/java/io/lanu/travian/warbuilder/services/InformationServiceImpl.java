package io.lanu.travian.warbuilder.services;

import com.gargoylesoftware.htmlunit.html.*;
import io.lanu.travian.warbuilder.models.VillageModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InformationServiceImpl implements InformationService {

    private SharedService sharedService;

    public InformationServiceImpl(SharedService sharedService) {
        this.sharedService = sharedService;
    }

    @Override
    public List<VillageModel> getAllVillages(){

        if (sharedService.isLoggedOut()){sharedService.login();}

        HtmlPage currentPage = sharedService.getPage("dorf2.php");
        List<HtmlAnchor> anchors = currentPage
                .getByXPath("//div[@id='sidebarBoxVillagelist']//div[@class='innerBox content']//a");

        return anchors.stream()
                .map(htmlAnchor -> {
                    HtmlDivision d = (HtmlDivision) htmlAnchor.getElementsByAttribute("div", "class", "name").get(0);
                    return new VillageModel(d.getTextContent(), htmlAnchor.getHrefAttribute());
                }).collect(Collectors.toList());
    }

    @Override
    public Integer[] getAvailableTroops(String villageName){

        HtmlPage currentPage;
        Integer[] result = new Integer[11];

        if (sharedService.isLoggedOut()){sharedService.login();}

        String id = getAllVillages().stream()
                .filter(villageModel -> villageModel.getName().equals(villageName))
                .findFirst().get().getId();
        sharedService.getPage("dorf2.php" + id);
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
