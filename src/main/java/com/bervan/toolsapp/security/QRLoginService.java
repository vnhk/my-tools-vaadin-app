package com.bervan.toolsapp.security;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.vaadin.flow.server.VaadinRequest;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class QRLoginService {

    private final Map<String, QRLoginData> qrLoginSessions = new ConcurrentHashMap<>();

    public QRLoginData generateQRLogin() {
        String uuid = UUID.randomUUID().toString();
        int number = ThreadLocalRandom.current().nextInt(1, 100); // 01-99
        QRLoginData data = new QRLoginData(uuid, number);
        qrLoginSessions.put(uuid, data);

        // Clean expired sessions
        cleanExpiredSessions();

        return data;
    }

    public byte[] generateQRCode(String url) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, 200, 200);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        return outputStream.toByteArray();
    }

    public QRLoginData getQRLoginData(String uuid) {
        return qrLoginSessions.get(uuid);
    }

    public boolean validateAndConsumeQRLogin(String uuid, int enteredNumber) {
        QRLoginData data = qrLoginSessions.get(uuid);
        if (data == null || data.isExpired() || data.isUsed()) {
            return false;
        }

        if (data.getNumber() == enteredNumber) {
            data.setUsed(true);
            return true;
        }

        return false;
    }

    private void cleanExpiredSessions() {
        qrLoginSessions.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    public String buildFullUrl(String uuid) {
        VaadinRequest request = VaadinRequest.getCurrent();
        if (request != null) {
            String scheme = request.getHeader("X-Forwarded-Proto");
            if (scheme == null) {
                scheme = request.isSecure() ? "https" : "http";
            }

            String host = request.getHeader("X-Forwarded-Host");
            if (host == null) {
                host = request.getHeader("Host");
            }
            if (host == null) {
                host = request.getRemoteHost();
                int port = request.getRemotePort();
                if ((scheme.equals("http") && port != 80) || (scheme.equals("https") && port != 443)) {
                    host = host + ":" + port;
                }
            }

            return scheme + "://" + host + "/accept-login/" + uuid;
        }

        throw new RuntimeException("Unable to build full URL!");
    }

    public static class QRLoginData {
        private final String uuid;
        private final int number;
        private final long createdTime;
        private boolean used;

        public QRLoginData(String uuid, int number) {
            this.uuid = uuid;
            this.number = number;
            this.createdTime = System.currentTimeMillis();
            this.used = false;
        }

        public String getUuid() {
            return uuid;
        }

        public int getNumber() {
            return number;
        }

        public long getCreatedTime() {
            return createdTime;
        }

        public boolean isUsed() {
            return used;
        }

        public void setUsed(boolean used) {
            this.used = used;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - createdTime > 300000; // 5 minutes
        }
    }

}