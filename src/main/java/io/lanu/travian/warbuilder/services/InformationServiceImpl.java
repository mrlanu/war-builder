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
    public void changeActiveVillage(String attackingVillageName){
        getAllVillages().stream()
                .filter(villageModel -> villageModel.getName().equals(attackingVillageName))
                .findFirst().ifPresent(villageModel -> {
            sharedService.getPage("dorf2.php" + villageModel.getId());
            sharedService.getPage("build.php?tt=2&id=39");
        });
    }

    @Override
    public List<VillageModel> getAllVillages(){

        if (sharedService.isLoggedOut()){sharedService.login();}

        HtmlPage currentPage = sharedService.getPage("dorf2.php");
        List<HtmlAnchor> anchors = currentPage
                .getByXPath("//div[@id='sidebarBoxVillagelist']//div[@class='content']//a");

        return anchors.stream()
                .map(htmlAnchor -> {
                    HtmlSpan d = (HtmlSpan) htmlAnchor.getElementsByAttribute("span", "class", "name").get(0);
                    return new VillageModel(d.getTextContent(), htmlAnchor.getHrefAttribute());
                }).collect(Collectors.toList());
    }

    @Override
    public Integer[] getAvailableTroops(String villageName){

        HtmlPage currentPage;
        Integer[] result = new Integer[11];

        if (sharedService.isLoggedOut()){sharedService.login();}

        getAllVillages().stream()
                .filter(villageModel -> villageModel.getName().equals(villageName))
                .findFirst().ifPresent(villageModel -> {
                    sharedService.getPage("dorf2.php" + villageModel.getId());
                    List<HtmlElement> nodes = sharedService.getPage("build.php?tt=1&id=39")
                            .getByXPath("//table[@class='troop_details']");

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
                });

        //sharedService.logout();

        return result;
    }
}
