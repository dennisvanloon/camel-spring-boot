package com.dvl.camelsftp.sftp;

public class SftpServiceException extends Exception {

    public SftpServiceException(String errorMessage, Throwable error) {
        super(errorMessage, error);
    }

}
