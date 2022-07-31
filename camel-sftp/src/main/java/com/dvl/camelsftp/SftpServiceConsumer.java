package com.dvl.camelsftp;

import com.dvl.camelsftp.sftp.SftpServiceException;
import com.dvl.camelsftp.sftp.SftpServiceJSchImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    private final SftpServiceJSchImpl sftpServiceJSchImpl;
    private final int maxNumberOfFiles;

    public SftpServiceConsumer(@Value("${consumer.enabled}") boolean enabled, @Value("${consumer.maxNumberOfFiles}") int maxNumberOfFiles,
                               @Value("${consumer.initialDelay}") int initialDelay, @Value("${consumer.period}") int period,
                               SftpServiceJSchImpl sftpServiceJSchImpl) {
        this.sftpServiceJSchImpl = sftpServiceJSchImpl;
        this.maxNumberOfFiles = maxNumberOfFiles;

        if(enabled) {
            log.info("scheduling to do the work every {} seconds, starting after {} seconds.", period, initialDelay);
            ScheduledExecutorService executorService = newScheduledThreadPool(1);
            executorService.scheduleAtFixedRate(() -> {
                try {
                    doTheWork();
                } catch (SftpServiceException e) {
                    log.error("", e);
                }
            }, 10, 30, SECONDS);
        } else {
            log.info("Not scheduling to do the work, disabled.");
        }
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
