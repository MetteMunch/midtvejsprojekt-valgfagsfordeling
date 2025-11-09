package mmh.valgfagsfordeling.controller;

import mmh.valgfagsfordeling.service.AdministrationService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdministrationController {

    private final AdministrationService administrationService;

    public AdministrationController(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }


}
