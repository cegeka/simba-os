package org.simbasecurity.core.service.communication.mail.template;

import com.google.common.collect.ImmutableMap;
import org.simbasecurity.core.domain.Language;

import java.util.Collections;
import java.util.Map;

public interface TemplateService {

    default String createMailBodyWithLink(TemplateWithLink templateWithLink, Language language) {
        return parseTemplate(templateWithLink.getTemplate(), language, ImmutableMap.of("link", templateWithLink.getLink()));
    }

    default String createMailSubject(String template, Language language) {
        return parseTemplate(template, language, Collections.emptyMap());
    }

    String parseTemplate(String template, Language language, Map<String, ?> properties);

}
