package io.lanu.travian.warbuilder.services;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Player {
    private String clientId;
    private String travianUserName;
    private String travianPass;
}
