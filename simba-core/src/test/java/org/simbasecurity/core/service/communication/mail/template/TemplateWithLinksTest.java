package org.simbasecurity.core.service.communication.mail.template;

import org.junit.Test;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TemplateWithLinksTest {

    @Test
    public void getLinksShouldReturnUrlsAsStrings() throws Exception {
        URL link1 = new URL("http", "www.simba.be", 1000, "/badumtsss");
        URL link2 = new URL("http", "www.dag.no", 80, "/FAEN");
        List<URL> links = Arrays.asList(link1, link2);
        TemplateWithLinks templateWithLinks = new TemplateWithLinks("template", links);

        assertThat(templateWithLinks.getLinks()).containsExactly("http://www.simba.be:1000/badumtsss", "http://www.dag.no:80/FAEN");
    }

}