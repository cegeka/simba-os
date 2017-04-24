/*
 * Copyright 2013-2017 Simba Open Source
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
 *
 */
package org.simbasecurity.core.event;


import static org.mockito.Mockito.*;

import java.util.EnumSet;
import javax.jms.ObjectMessage;
import javax.jms.Topic;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jms.core.JmsTemplate;

@RunWith(MockitoJUnitRunner.class)
public class EventServiceTest {

    @Mock private org.simbasecurity.core.event.SimbaEventListener ruleChangedInterestListener;
    @Mock private org.simbasecurity.core.event.SimbaEventListener noInterestListener;

    @Mock private JmsTemplate jmsTemplateMock;
    @Mock private Topic jmsTopicMock;
    @Mock private ObjectMessage messageMock;
    @Mock private SimbaEvent eventMock;

    @InjectMocks
    private EventService eventService;

    @Before
    public void setUp() throws Exception {
        when(ruleChangedInterestListener.getTypesOfInterest()).thenReturn(EnumSet.of(SimbaEventType.RULE_CHANGED));
        when(noInterestListener.getTypesOfInterest()).thenReturn(EnumSet.noneOf(SimbaEventType.class));

        eventService.postProcessAfterInitialization(ruleChangedInterestListener, "ruleChangedInterest");
        eventService.postProcessAfterInitialization(noInterestListener, "noInterest");
    }

    @Test
    public void onMessageNotifiesOnlyListenersWithCorrectInterest() throws Exception {

        when(messageMock.getObject()).thenReturn(eventMock);
        when(eventMock.getEventType()).thenReturn(SimbaEventType.RULE_CHANGED);

        eventService.onMessage(messageMock);

        verify(noInterestListener, never()).eventOccured(any(SimbaEvent.class));
        verify(ruleChangedInterestListener).eventOccured(eventMock);
    }

}
