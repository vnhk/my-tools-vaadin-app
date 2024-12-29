package com.bervan.toolsapp.views.otpview;

import com.bervan.common.service.AuthService;
import com.bervan.toolsapp.security.OTPService;
import com.bervan.toolsapp.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.PermitAll;

import java.util.UUID;

@Route(value = "generate-otp", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
public class OTPGenerateView extends VerticalLayout {

    private final OTPService otpService;
    private final H3 otpLabel;

    public OTPGenerateView(OTPService otpService) {
        this.otpService = otpService;
        this.otpLabel = new H3("Click the button to generate OTP");

        addClassName("otp-generate-view");
        setSpacing(true);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        //add dropdown (default STREAMING) STREAMING, READ, WRITE, that will generate code
        //and role and in authprovider we set role STREAMING_ONLY that means we can only access streaming app
        //it has to be done by method secr

        //generate otp as button visible in all views (like bucket) and code determines also url and after login
        //user is navigated there

        Button generateOtpButton = new Button("Generate OTP", event -> generateAndDisplayOTP());

        add(otpLabel, generateOtpButton);
    }

    private void generateAndDisplayOTP() {
        UUID userId = AuthService.getLoggedUserId();
        String otpCode = otpService.generateOTP(userId);
        otpLabel.setText("Your OTP code is: " + otpCode);
    }
}