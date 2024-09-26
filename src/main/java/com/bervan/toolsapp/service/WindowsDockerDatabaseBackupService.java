package com.bervan.toolsapp.service;

import com.bervan.core.model.BervanLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;

@Service
public class WindowsDockerDatabaseBackupService {

    private final BervanLogger log;
    @Value("${db.container.name}")
    private String containerName;

    @Value("#{environment.SPRING_DATASOURCE_USERNAME}")
    private String dbUser;

    @Value("#{environment.SPRING_DATASOURCE_PASSWORD}")
    private String dbPassword;

    @Value("#{environment.SPRING_DATASOURCE_DATABASE_NAME}")
    private String dbName;

    @Value("${file.service.storage.folder}")
    private String pathToFileStorage;

    @Value("${backup.file-storage-relative-path}")
    private String backupStoragePath;

    public WindowsDockerDatabaseBackupService(BervanLogger log) {
        this.log = log;
    }

    public boolean backupDatabase() {
        try {
            String command = "docker exec " + containerName + " mariadb-dump -u" + dbUser + " -p" + dbPassword + " " + dbName + " > " +
                    pathToFileStorage + backupStoragePath + File.separator + "database_dump_" + LocalDateTime.now() + ".sql";

            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);

            processBuilder.inheritIO();

            Process process = processBuilder.start();

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                log.info("Backup database completed successfully.");
                return true;
            } else {
                log.error("Backup database failed with exit code " + exitCode);
            }
        } catch (Exception e) {
            log.error("Backup database failed with exit code " + e.getMessage(), e);
        }
        return false;
    }
}