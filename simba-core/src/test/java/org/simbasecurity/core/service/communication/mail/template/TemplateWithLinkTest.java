package org.simbasecurity.core.service.communication.mail.template;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.net.URL;

public class TemplateWithLinkTest {


    @Test
    public void getLinkShouldReturnUrlAsString() throws Exception {
        TemplateWithLink templateWithLink=new TemplateWithLink("template",new URL("http","www.simba.be",1000,"/badumtsss"));
        Assertions.assertThat(templateWithLink.getLink()).isEqualTo("http://www.simba.be:1000/badumtsss");
    }


}