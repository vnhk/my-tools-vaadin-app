package com.bervan.toolsapp.views;

import com.bervan.common.AbstractPageView;
import com.bervan.toolsapp.security.QRLoginService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.StreamResource;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;

@Slf4j
public class QRLoginView extends AbstractPageView {

    private final QRLoginService qrLoginService;
    private VerticalLayout qrLayout;

    public QRLoginView(QRLoginService qrLoginService) {
        this.qrLoginService = qrLoginService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        initializeView();
    }

    private void initializeView() {
        H2 title = new H2("QR Code Login");
        Paragraph instruction = new Paragraph("Scan the QR code and enter the number shown below to login:");

        qrLayout = new VerticalLayout();
        qrLayout.setAlignItems(Alignment.CENTER);

        Button generateButton = new Button("Generate New QR Code", e -> generateQRCode());
        generateButton.addClassName("primary");

        Button backButton = new Button("Back to Login", e ->
                getUI().ifPresent(ui -> ui.navigate("/login")));

        Div buttonLayout = new Div(generateButton, backButton);
        buttonLayout.addClassName("button-layout");

        add(title, instruction, qrLayout, buttonLayout);

        // Generate initial QR code
        generateQRCode();
    }

    private void generateQRCode() {
        try {
            QRLoginService.QRLoginData qrData = qrLoginService.generateQRLogin();
            String url = qrLoginService.buildFullUrl(qrData.getUuid());
            byte[] qrCodeBytes = qrLoginService.generateQRCode(url);

            StreamResource resource = new StreamResource("qr-code.png",
                    () -> new ByteArrayInputStream(qrCodeBytes));

            Image qrImage = new Image(resource, "QR Code");
            qrImage.setWidth("200px");
            qrImage.setHeight("200px");

            Paragraph numberLabel = new Paragraph(String.format("Number: %02d", qrData.getNumber()));
            numberLabel.getStyle().set("font-size", "24px");
            numberLabel.getStyle().set("font-weight", "bold");
            numberLabel.getStyle().set("color", "var(--lumo-primary-color)");

            qrLayout.removeAll();
            qrLayout.add(qrImage, new Text(url), numberLabel);

        } catch (Exception e) {
            showErrorNotification("Error generating QR code: " + e.getMessage());
            log.error("Error generating QR code!", e);
        }
    }
}