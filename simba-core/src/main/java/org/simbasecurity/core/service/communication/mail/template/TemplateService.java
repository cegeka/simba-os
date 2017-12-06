package org.simbasecurity.core.service.communication.mail.template;

import java.util.Map;

public interface TemplateService {
    String createMailBody(String template, Map<String, String> properties);
}
