package org.simbasecurity.core.service.communication.mail.template;

import com.google.common.collect.ImmutableMap;
import org.simbasecurity.core.domain.Language;

import java.net.URL;
import java.util.Map;

public interface TemplateService {

    default String createMailBodyWithLink(String template, Language language, URL link) {
        return createMailBody(template, language, ImmutableMap.of("link", link.toString()));
    }

    String createMailBody(String template, Language language, Map<String, String> properties);
}
