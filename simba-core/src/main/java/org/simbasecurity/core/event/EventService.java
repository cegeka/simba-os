/*
 * Copyright 2013 Simba Open Source
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.simbasecurity.core.event;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;

import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

@Service
public class EventService implements MessageListener, BeanPostProcessor {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(EventService.class);

    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private Topic topic;

    private JmsTemplate jmsTemplate;

    @PostConstruct
    public void createJMSTemplate() {
        jmsTemplate = new JmsTemplate(connectionFactory);
    }

    private EnumMap<SimbaEventType, List<SimbaEventListener>> registeredListeners = new EnumMap<SimbaEventType, List<SimbaEventListener>>(
            SimbaEventType.class);

    public void publish(final SimbaEventType eventType) {
        publish(eventType, null);
    }

    public void publish(final SimbaEventType eventType, final String userName) {
        publish(new SimbaEvent(eventType, userName));
    }

    public void publish(final SimbaEventType eventType, final String parameterName, final Object parameterOldValue, final Object parameterNewValue) {
        publish(new SimbaEvent(eventType, parameterName, new Pair(parameterOldValue, parameterNewValue)));
    }

    private void publish(final SimbaEvent simbaEvent) {
        jmsTemplate.send(this.topic, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                ObjectMessage message = session.createObjectMessage();
                message.setObject(simbaEvent);
                return message;
            }
        });
    }

    @Override
    public void onMessage(Message message) {
        try {
            SimbaEvent event = (SimbaEvent) ((ObjectMessage) message).getObject();
            if (event != null) {
                SimbaEventType eventType = event.getEventType();
                List<SimbaEventListener> list = registeredListeners.get(eventType);
                if (list != null && !list.isEmpty()) {
                    LOG.debug("Notifying registered listeners of '{}' event.", eventType);
                    for (SimbaEventListener eventListener : list) {
                        eventListener.eventOccured(event);
                    }
                }
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof SimbaEventListener) {
            SimbaEventListener listener = (SimbaEventListener) bean;

            for (SimbaEventType eventType : listener.getTypesOfInterest()) {
                List<SimbaEventListener> list = registeredListeners.get(eventType);
                if (list == null) {
                    list = new LinkedList<SimbaEventListener>();
                    registeredListeners.put(eventType, list);
                }
                list.add(listener);
                LOG.debug("{} registered interest in events of type '{}'", beanName, eventType);
            }
        }
        return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

}
