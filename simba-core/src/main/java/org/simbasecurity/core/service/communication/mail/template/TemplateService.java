package org.simbasecurity.core.service.communication.mail.template;

import org.simbasecurity.core.domain.Language;

import java.util.Map;

public interface TemplateService {
    String createMailBody(String template, Language language, Map<String, String> properties);
}
