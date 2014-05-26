package org.simbasecurity.core.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageListener;
import javax.jms.Topic;

@Configuration
public class EventServiceConfiguration {

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    @Qualifier("eventService")
    private MessageListener messageListener;

    @Autowired
    @Qualifier("simbaEventTopic")
    private Destination destination;

    @Bean
    public DefaultMessageListenerContainer jmsContainer() {
        DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setDestination(destination);
        container.setMessageListener(messageListener);

        container.start();

        return container;
    }
}
