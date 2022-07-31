package com.dvl.camelsftp.sftp;

import java.io.File;
import java.util.List;

public interface SftpService {

    void putFile(File file, String remoteFilename) throws SftpServiceException;

    void deleteFiles(List<String> remoteFilesToRemove) throws SftpServiceException;

    List<String>  listFiles() throws SftpServiceException;

}
