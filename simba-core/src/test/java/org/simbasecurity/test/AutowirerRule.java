package org.simbasecurity.test;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.simbasecurity.core.spring.AutowireHelper;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.RequiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.weaving.LoadTimeWeaverAwareProcessor;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;

import static org.mockito.Mockito.mock;

public class AutowirerRule extends TestWatcher {

    private DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

    private AutowirerRule() {
        AutowiredAnnotationBeanPostProcessor beanPostProcessor = new AutowiredAnnotationBeanPostProcessor();
        beanPostProcessor.setBeanFactory(beanFactory);
        beanFactory.addBeanPostProcessor(beanPostProcessor);
        beanFactory.addBeanPostProcessor(new PersistenceAnnotationBeanPostProcessor());
        beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor());
        beanFactory.addBeanPostProcessor(new RequiredAnnotationBeanPostProcessor());
        beanFactory.addBeanPostProcessor(new CommonAnnotationBeanPostProcessor());
    }

    public static AutowirerRule autowirer() {
        return new AutowirerRule();
    }

    public void registerBean(Object bean) {
        beanFactory.registerSingleton(bean.getClass().getSimpleName(), bean);
    }

    public <T> T mockBean(Class<T> aClass) {
        beanFactory.registerBeanDefinition(aClass.getSimpleName(), BeanDefinitionBuilder.genericBeanDefinition(aClass).getBeanDefinition());
        T mock = mock(aClass);
        beanFactory.registerSingleton(aClass.getSimpleName(), mock);
        return mock;
    }

    @Override
    protected void starting(Description description) {
        new AutowireHelper().setAutowirer(beanFactory);
    }

    @Override
    protected void finished(Description description) {
        new AutowireHelper().setAutowirer(null);
    }


}
