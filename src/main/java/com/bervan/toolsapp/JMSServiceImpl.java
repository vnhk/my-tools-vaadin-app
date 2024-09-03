package com.bervan.toolsapp;

import com.bervan.common.service.JMSService;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Service;

@Service
public class JMSServiceImpl extends JMSService {
    private final JmsTemplate jmsTemplate;

    public JMSServiceImpl(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void convertAndSend(String queueName, Object object, String jmsGroupId) {
        MessagePostProcessor messagePostProcessor = message -> {
            if (jmsGroupId != null) {
                message.setStringProperty("JMSXGroupID", jmsGroupId);
            }

            return message;
        };

        jmsTemplate.convertAndSend(queueName, object, messagePostProcessor);
    }
}
