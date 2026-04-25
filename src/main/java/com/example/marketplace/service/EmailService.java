package com.example.marketplace.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Thin wrapper around the SendGrid API. Handles password-reset emails today;
 * extend with more sendXxx methods as transactional email use cases grow.
 *
 * If sendgrid.api-key is empty (e.g. local dev), {@link #send(String, String, String, String)}
 * returns false and logs the payload — calling code should still treat the
 * password-reset request as successful so the user isn't penalized.
 */
@Slf4j
@Service
public class EmailService {

    private final String apiKey;
    private final String fromEmail;
    private final String fromName;

    public EmailService(
            @Value("${sendgrid.api-key:}") String apiKey,
            @Value("${sendgrid.from-email:no-reply@yayaconnectapp.com}") String fromEmail,
            @Value("${sendgrid.from-name:YayaConnect}") String fromName
    ) {
        this.apiKey = apiKey;
        this.fromEmail = fromEmail;
        this.fromName = fromName;
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }

    /** Send an HTML email. Returns true on 2xx, false otherwise. */
    public boolean send(String toEmail, String toName, String subject, String htmlBody) {
        if (!isConfigured()) {
            log.warn("SendGrid not configured; would send '{}' to {}. Body:\n{}", subject, toEmail, htmlBody);
            return false;
        }
        if (toEmail == null || toEmail.isBlank()) {
            log.warn("Skipping email '{}' — recipient has no email address.", subject);
            return false;
        }

        Email from = new Email(fromEmail, fromName);
        Email to = toName == null ? new Email(toEmail) : new Email(toEmail, toName);
        Content content = new Content("text/html", htmlBody);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            int status = response.getStatusCode();
            if (status >= 200 && status < 300) return true;
            log.error("SendGrid send failed: status={} body={}", status, response.getBody());
            return false;
        } catch (IOException e) {
            log.error("SendGrid request failed for '{}' → {}", subject, toEmail, e);
            return false;
        }
    }

    public boolean sendPasswordReset(String toEmail, String toName, String resetUrl, long ttlMinutes) {
        String subject = "Reset your YayaConnect password";
        String html = passwordResetTemplate(toName, resetUrl, ttlMinutes);
        return send(toEmail, toName, subject, html);
    }

    public boolean sendPasswordResetOtp(String toEmail, String toName, String code, long ttlMinutes) {
        String subject = "Your YayaConnect reset code";
        String html = otpTemplate(toName, code, ttlMinutes);
        return send(toEmail, toName, subject, html);
    }

    private String otpTemplate(String name, String code, long ttlMinutes) {
        String safeName = name == null || name.isBlank() ? "there" : escapeHtml(name);
        String safeCode = escapeHtml(code);
        return "<!doctype html><html><body style=\"margin:0;padding:0;background:#f8fafc;font-family:-apple-system,Segoe UI,Roboto,Helvetica,Arial,sans-serif;color:#0f172a;\">"
                + "<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background:#f8fafc;padding:32px 16px;\">"
                + "<tr><td align=\"center\">"
                + "<table role=\"presentation\" width=\"480\" cellpadding=\"0\" cellspacing=\"0\" style=\"max-width:480px;background:#ffffff;border-radius:14px;overflow:hidden;box-shadow:0 1px 3px rgba(15,23,42,0.06);\">"
                + "<tr><td style=\"background:linear-gradient(135deg,#00ACC1 0%,#1a3c34 100%);padding:24px 28px;color:#ffffff;\">"
                + "<div style=\"font-size:18px;font-weight:700;letter-spacing:-0.01em;\">YayaConnect</div>"
                + "<div style=\"font-size:14px;opacity:0.9;margin-top:2px;\">Reset code</div>"
                + "</td></tr>"
                + "<tr><td style=\"padding:28px;\">"
                + "<p style=\"font-size:15px;line-height:1.5;margin:0 0 16px;\">Hi " + safeName + ",</p>"
                + "<p style=\"font-size:15px;line-height:1.5;margin:0 0 20px;\">Use the code below to reset your YayaConnect password.</p>"
                + "<div style=\"text-align:center;margin:24px 0;\">"
                + "<div style=\"display:inline-block;background:#f1f5f9;border:1px solid #e5e7eb;border-radius:12px;padding:16px 28px;font-family:'SF Mono',Menlo,Monaco,Consolas,monospace;font-size:32px;font-weight:700;letter-spacing:0.4em;color:#0f172a;\">" + safeCode + "</div>"
                + "</div>"
                + "<p style=\"font-size:13px;color:#6b7280;line-height:1.5;margin:0;\">This code expires in " + ttlMinutes + " minutes. If you didn't request a reset, you can ignore this email — your password won't change.</p>"
                + "</td></tr>"
                + "<tr><td style=\"background:#f8fafc;padding:16px 28px;border-top:1px solid #e5e7eb;font-size:12px;color:#6b7280;\">"
                + "Sent by YayaConnect · Nairobi, Kenya"
                + "</td></tr>"
                + "</table></td></tr></table></body></html>";
    }

    private String passwordResetTemplate(String name, String url, long ttlMinutes) {
        String safeName = name == null || name.isBlank() ? "there" : escapeHtml(name);
        String safeUrl = escapeHtml(url);
        return "<!doctype html><html><body style=\"margin:0;padding:0;background:#f8fafc;font-family:-apple-system,Segoe UI,Roboto,Helvetica,Arial,sans-serif;color:#0f172a;\">"
                + "<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background:#f8fafc;padding:32px 16px;\">"
                + "<tr><td align=\"center\">"
                + "<table role=\"presentation\" width=\"480\" cellpadding=\"0\" cellspacing=\"0\" style=\"max-width:480px;background:#ffffff;border-radius:14px;overflow:hidden;box-shadow:0 1px 3px rgba(15,23,42,0.06);\">"
                + "<tr><td style=\"background:linear-gradient(135deg,#00ACC1 0%,#1a3c34 100%);padding:24px 28px;color:#ffffff;\">"
                + "<div style=\"font-size:18px;font-weight:700;letter-spacing:-0.01em;\">YayaConnect</div>"
                + "<div style=\"font-size:14px;opacity:0.9;margin-top:2px;\">Password reset</div>"
                + "</td></tr>"
                + "<tr><td style=\"padding:28px;\">"
                + "<p style=\"font-size:15px;line-height:1.5;margin:0 0 16px;\">Hi " + safeName + ",</p>"
                + "<p style=\"font-size:15px;line-height:1.5;margin:0 0 20px;\">We received a request to reset the password on your YayaConnect account. Click the button below to choose a new password.</p>"
                + "<p style=\"margin:24px 0;\">"
                + "<a href=\"" + safeUrl + "\" style=\"display:inline-block;background:#00ACC1;color:#ffffff;text-decoration:none;font-weight:600;padding:12px 24px;border-radius:10px;\">Reset password</a>"
                + "</p>"
                + "<p style=\"font-size:13px;color:#6b7280;line-height:1.5;margin:0 0 12px;\">This link expires in " + ttlMinutes + " minutes. If you didn't request a reset, you can ignore this email — your password won't change.</p>"
                + "<p style=\"font-size:12px;color:#94a3b8;line-height:1.5;margin:16px 0 0;word-break:break-all;\">Or paste this link in your browser:<br/>" + safeUrl + "</p>"
                + "</td></tr>"
                + "<tr><td style=\"background:#f8fafc;padding:16px 28px;border-top:1px solid #e5e7eb;font-size:12px;color:#6b7280;\">"
                + "Sent by YayaConnect · Nairobi, Kenya"
                + "</td></tr>"
                + "</table></td></tr></table></body></html>";
    }

    private String escapeHtml(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
