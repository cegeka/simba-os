package org.simbasecurity.core.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

@Component
public class AutowireHelper {

    private static AutowireCapableBeanFactory autowirer;

    @Autowired
    public void setAutowirer(AutowireCapableBeanFactory autowirer) {
        AutowireHelper.autowirer = autowirer;
    }

    public static void autowireBean(Object object) {
        if (autowirer != null) autowirer.autowireBean(object);
    }
}
