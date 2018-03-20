package org.simbasecurity.core.spring;

import org.simbasecurity.core.domain.AbstractEntity;
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

    public static void autowireBean(AbstractEntity abstractEntity) {
        if (autowirer != null) autowirer.autowireBean(abstractEntity);
    }
}
