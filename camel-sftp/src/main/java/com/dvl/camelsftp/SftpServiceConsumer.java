package com.dvl.camelsftp;

import com.dvl.camelsftp.sftp.SftpServiceException;
import com.dvl.camelsftp.sftp.SftpServiceJSchImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.SECONDS;

@Component
@Slf4j
public class SftpServiceConsumer {

    private final int maxNumberOfFiles = 5;

    private final SftpServiceJSchImpl sftpServiceJSchImpl;

    public SftpServiceConsumer(SftpServiceJSchImpl sftpServiceJSchImpl) {
        this.sftpServiceJSchImpl = sftpServiceJSchImpl;

        ScheduledExecutorService executorService = newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(() -> {
            try {
                doTheWork();
            } catch (SftpServiceException e) {
                log.error("", e);
            }
        }, 10, 30, SECONDS);
    }

    private void doTheWork() throws SftpServiceException {
        try {
            File file = File.createTempFile("test-", ".txt");
            sftpServiceJSchImpl.putFile(file, "test-" + UUID.randomUUID() + ".txt");
        } catch (Exception e) {
            log.error("Error putting file", e);
        }

        List<String> filenames = sftpServiceJSchImpl.listFiles();
        log.info("Found the following files on the server: {}", filenames);

        List<String> filesToRemove = filenames.subList(maxNumberOfFiles, filenames.size() - 1);
        log.info("Will remove the following files: {}", filesToRemove);

        sftpServiceJSchImpl.deleteFiles(filesToRemove);
    }

}
