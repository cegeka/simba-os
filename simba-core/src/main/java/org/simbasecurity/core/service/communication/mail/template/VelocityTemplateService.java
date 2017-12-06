package org.simbasecurity.core.service.communication.mail.template;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.util.Map;

public class VelocityTemplateService implements TemplateService {

    private VelocityEngine velocityEngine;

    public VelocityTemplateService(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    @Override
    public String createMailBody(String template, Map<String, String> properties) {
        VelocityContext context = new VelocityContext();
        for (String propertyName : properties.keySet()) {
            context.put(propertyName, properties.get(propertyName));
        }
        return mergeTemplate(template, context);
    }

    private String mergeTemplate(String template, VelocityContext context) {
        StringWriter writer = new StringWriter();
        try {
            velocityEngine.mergeTemplate("velocity/" + template, "UTF-8", context, writer);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Could not merge template %s because of %s", template, e.getMessage()));
        }
        return writer.toString();
    }
}
