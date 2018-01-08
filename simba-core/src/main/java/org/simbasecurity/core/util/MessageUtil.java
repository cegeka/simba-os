package org.simbasecurity.core.util;

import org.simbasecurity.core.domain.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class MessageUtil {

    private static final Logger LOG = LoggerFactory.getLogger(MessageUtil.class);

    private static final String MESSAGE_BUNDLE = "org.simbasecurity.messages.Message";

    public static String getResourceMessage(String resourceKey, Language language) {
        String resource = null;

        ResourceBundle resourceBundle = ResourceBundle.getBundle(MESSAGE_BUNDLE, new Locale(language.name()));
        if (resourceBundle != null) {
            try {
                resource = resourceBundle.getString(resourceKey);
            } catch (MissingResourceException ex) {
                resource = resourceKey;
                LOG.warn("{0} not found in bundle {1}", resourceKey, MESSAGE_BUNDLE);
            }
        }

        return resource;
    }

}
