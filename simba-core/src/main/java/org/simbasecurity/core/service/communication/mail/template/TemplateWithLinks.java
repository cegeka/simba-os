package org.simbasecurity.core.service.communication.mail.template;

import java.net.URL;
import java.util.List;
import java.util.Objects;

public class TemplateWithLinks {
    private final String template;
    private final List<URL> links;

    public TemplateWithLinks(String template, List<URL> links) {
        this.template = template;
        this.links = links;
    }

    public String getTemplate() {
        return template;
    }

    public String[] getLinks() {
        return links.stream().map(URL::toString).toArray(String[]::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TemplateWithLinks that = (TemplateWithLinks) o;
        return Objects.equals(template, that.template) &&
                Objects.equals(links, that.links);
    }

    @Override
    public int hashCode() {
        return Objects.hash(template, links);
    }
}
