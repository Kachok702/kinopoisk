package org.example.VKR.services;

import javax.mail.MessagingException;
import java.io.FileNotFoundException;

public interface EmailService {

    void sendEmailWithAttachment(final String toAddress, final String subject, final String message, final String attachment) throws MessagingException, FileNotFoundException;
}
