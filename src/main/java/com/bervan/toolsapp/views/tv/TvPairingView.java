package com.bervan.toolsapp.views.tv;

import com.bervan.common.MenuNavigationComponent;
import com.bervan.common.view.AbstractPageView;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple view that lets the user connect a TV device to the current account.
 *
 * User flow:
 * - Open this page in the main application.
 * - Enter pairing code shown on TV screen.
 * - Click "Connect TV".
 * - This view calls the backend endpoint /api/tv/pair/assign which:
 *   - creates a token for current user
 *   - sends it to the TV server using /api/tv/pair/confirm
 */
@Route(value = TvPairingView.ROUTE_NAME, layout = MainLayout.class)
@PageTitle("TV Pairing")
@RolesAllowed("USER")
public class TvPairingView extends AbstractPageView {

    public static final String ROUTE_NAME = "tv-pairing";

    private final RestTemplate restTemplate = new RestTemplate();

    private final TextField codeField = new TextField("TV Pairing Code");
    private final H3 infoLabel = new H3("Enter the code shown on your TV");

    public TvPairingView() {
        addClassName("tv-pairing-view");
        setSpacing(true);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        codeField.setMaxLength(10);
        codeField.setWidth("200px");
        codeField.setAutofocus(true);

        Button connectButton = new Button("Connect TV", event -> connectTv());
        connectButton.addClassName("option-button");

        HorizontalLayout row = new HorizontalLayout(codeField, connectButton);
        row.setAlignItems(Alignment.END);

        add(new MenuNavigationComponent("") {
        }, infoLabel, row);
    }

    private void connectTv() {
        String pairCode = codeField.getValue();
        if (pairCode == null || pairCode.isBlank()) {
            Notification.show("Please enter pairing code from TV.", 3000, Notification.Position.MIDDLE);
            return;
        }

        try {
            String url = "/api/tv/pair/assign";

            Map<String, String> body = new HashMap<>();
            body.put("pairCode", pairCode.trim());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            String httpBaseUrl = ServletUriComponentsBuilder //local communication
                    .fromCurrentContextPath()
                    .scheme("http")   // change scheme
                    .port(-1)         // remove port
                    .build()
                    .toUriString();

            ResponseEntity<String> response = restTemplate.postForEntity(httpBaseUrl + url, request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                Notification.show("TV successfully connected.", 3000, Notification.Position.MIDDLE);
            } else {
                String msg = response.getBody() != null ? response.getBody() : ("Error: " + response.getStatusCode());
                Notification.show("Failed to connect TV: " + msg, 4000, Notification.Position.MIDDLE);
            }
        } catch (Exception e) {
            Notification.show("Could not connect TV: " + e.getMessage(), 4000, Notification.Position.MIDDLE);
        }
    }
}

