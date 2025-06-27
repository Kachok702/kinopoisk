package org.example.VKR.config.JMS;


import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;

@Configuration
@EnableJms
public class JMSConfiguration {

    @Bean
    public Queue movieQueue(){
        return new ActiveMQQueue("movie.queue");
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        JmsTemplate template = new JmsTemplate(connectionFactory);
        template.setDefaultDestination(movieQueue());
        return template;
    }
}
