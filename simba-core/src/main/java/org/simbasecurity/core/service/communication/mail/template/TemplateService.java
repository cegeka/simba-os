package org.simbasecurity.core.service.communication.mail.template;

import com.google.common.collect.ImmutableMap;
import org.simbasecurity.core.domain.Language;

import java.util.Collections;
import java.util.Map;

public interface TemplateService {

    default String createMailBodyWithLink(TemplateWithLinks templateWithLinks, Language language) {
        return parseTemplate(templateWithLinks.getTemplate(), language, ImmutableMap.of("links", templateWithLinks.getLinks()));
    }

    default String createMailSubject(String template, Language language) {
        return parseTemplate(template, language, Collections.emptyMap());
    }

    String parseTemplate(String template, Language language, Map<String, ?> properties);

}
