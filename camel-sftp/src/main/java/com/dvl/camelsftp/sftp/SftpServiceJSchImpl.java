package com.dvl.camelsftp.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

@Component
@Slf4j
public class SftpServiceJSchImpl implements SftpService {

    @Value("${sftp.hostname}")
    private String hostname;

    @Value("${sftp.username}")
    private String username;

    @Value("${sftp.password}")
    private String password;

    @Value("${sftp.workingdir}")
    private String workingdir;

    @Override
    public void putFile(File file, String remoteFilename) throws SftpServiceException {
        log.info("Putting file {} to host {}", file, hostname);

        ChannelSftp channelSftp = setupJsch();
        try {
            channelSftp.put(file.getAbsolutePath(), remoteFilename);
        } catch (SftpException e) {
            throw new SftpServiceException("Error while putting file", e);
        }
        channelSftp.exit();

        log.info("Putting file {} to host {} done", file, hostname);
    }

    @Override
    public void deleteFiles(List<String> remoteFilesToRemove) throws SftpServiceException {
        log.info("Removing files {} from host {}", remoteFilesToRemove, hostname);

        ChannelSftp channelSftp = setupJsch();
        for (String file : remoteFilesToRemove) {
            log.info("Removing file {}", file);
            try {
                channelSftp.rm(file);
            } catch (SftpException e) {
                throw new SftpServiceException("Error while deleting file: " + file, e);
            }
        }
        channelSftp.exit();

        log.info("Removing files {} from host {} done", remoteFilesToRemove, hostname);
    }

    @Override
    public List<String> listFiles() throws SftpServiceException {
        log.info("Listing files on host {} from workingdir {}", hostname, workingdir);

        ChannelSftp channelSftp = setupJsch();
        List<String> filenames;
        try {
            Vector<ChannelSftp.LsEntry> result = channelSftp.ls(".");
            filenames = result.stream()
                    .filter(element -> !element.getAttrs().isDir())
                    .map(ChannelSftp.LsEntry::getFilename)
                    .toList();

        } catch (SftpException e) {
            throw new SftpServiceException("Error while listing files", e);
        }
        channelSftp.exit();

        log.info("Listing files on host {} done", hostname);
        return filenames;
    }

    private ChannelSftp setupJsch() throws SftpServiceException {
        JSch jsch = new JSch();
        try {
            Session session = jsch.getSession(username, hostname);
            session.setPassword(password);
            Properties properties = new Properties();
            properties.put("StrictHostKeyChecking", "no");
            session.setConfig(properties);
            session.connect();
            ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            channelSftp.cd(workingdir);
            return channelSftp;
        } catch(JSchException | SftpException e) {
            throw new SftpServiceException("Error setting up sftp connection", e);
        }
    }
}
