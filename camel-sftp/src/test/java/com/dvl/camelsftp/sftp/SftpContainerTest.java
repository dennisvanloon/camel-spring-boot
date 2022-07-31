package com.dvl.camelsftp.sftp;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class SftpContainerTest {

    private static final DockerImageName SFTP_IMAGE = DockerImageName.parse("atmoz/sftp");
    private static final GenericContainer sftpContainer;

    static {
        sftpContainer = new GenericContainer(SFTP_IMAGE).withExposedPorts(22);
        //TODO get the command from the property values
        sftpContainer.withCommand("sftpuser:password:::upload");
        sftpContainer.start();
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("sftp.port", () -> sftpContainer.getMappedPort(22));
    }

}
