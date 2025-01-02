package com.bervan.toolsapp.views;

import com.bervan.common.AbstractPageView;
import com.bervan.common.service.AuthService;
import com.bervan.toolsapp.security.OTPService;
import com.bervan.toolsapp.security.OtpAuthenticationToken;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import static com.bervan.toolsapp.security.OTPService.CODE_LENGTH;

@Route("login")
@PageTitle("Login")
@PermitAll
public class LoginView extends AbstractPageView {

    private final OTPService otpService;
    private final AuthenticationManager authenticationManager;

    public LoginView(OTPService otpService, AuthenticationManager authenticationManager) {
        this.otpService = otpService;
        this.authenticationManager = authenticationManager;
        addClassName("login-page");

        LoginForm loginForm = new LoginForm();
        loginForm.setAction("login");

        Button otpLoginButton = new Button("Login via OTP", event -> openOtpDialog());
        otpLoginButton.addClassName("option-button");
        otpLoginButton.addClassName("option-button-warning");

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        add(loginForm, otpLoginButton);
    }

    private void openOtpDialog() {
        Dialog otpDialog = new Dialog();
        otpDialog.setHeaderTitle("Enter OTP Code");

        VerticalLayout dialogLayout = new VerticalLayout();
        H3 instruction = new H3("Please enter the " + CODE_LENGTH + "-digit OTP code:");
        TextField otpField = new TextField("OTP Code");
        otpField.setMaxLength(CODE_LENGTH);
        otpField.setWidthFull();

        otpField.addValueChangeListener(event -> {
            String otpCode = event.getValue();
            if (otpCode.length() == CODE_LENGTH) {
                try {
                    loginViaOTP(otpCode);
                    otpDialog.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    showErrorNotification("Incorrect code!");
                    otpField.clear();
                }
            }
        });

        dialogLayout.add(instruction, otpField);
        otpDialog.add(dialogLayout);

        Button cancelButton = new Button("Cancel", e -> otpDialog.close());
        otpDialog.getFooter().add(cancelButton);

        otpDialog.open();
    }

    private void loginViaOTP(String otpCode) {
        OtpAuthenticationToken authReq = new OtpAuthenticationToken(
                otpCode,
                null,
                otpCode
        );

        Authentication authResult = authenticationManager.authenticate(authReq);
        SecurityContextHolder.getContext().setAuthentication(authResult);
        VaadinSession.getCurrent().getSession().setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );

        if (AuthService.getUserRole().equals("ROLE_STREAMING")) {
            getUI().ifPresent(ui -> ui.navigate("/streaming-platform"));
        } else {
            getUI().ifPresent(ui -> ui.navigate(""));
        }
    }
}