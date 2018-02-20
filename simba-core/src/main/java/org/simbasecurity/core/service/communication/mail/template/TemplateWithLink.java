package org.simbasecurity.core.service.communication.mail.template;

import java.net.URL;
import java.util.Objects;

public class TemplateWithLink {
    private final String template;
    private final URL link;

    public TemplateWithLink(String template, URL link) {
        this.template = template;
        this.link = link;
    }

    public String getTemplate() {
        return template;
    }

    public String getLink() {
        return link.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TemplateWithLink that = (TemplateWithLink) o;
        return Objects.equals(template, that.template) &&
                Objects.equals(link, that.link);
    }

    @Override
    public int hashCode() {

        return Objects.hash(template, link);
    }
}
