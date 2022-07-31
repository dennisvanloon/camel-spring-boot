package com.dvl.camelsftp.sftp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SftpServiceJSchImplTest extends SftpContainerTest  {

    @Autowired
    SftpService sftpService;

    @Test
    void listFiles() throws SftpServiceException {
        sftpService.listFiles();
        Assertions.assertTrue(true);
    }

}
