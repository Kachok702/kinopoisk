package org.example.VKR.config.JMS;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.VKR.models.Movie;
import org.example.VKR.rerpositories.MoviesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import javax.annotation.PostConstruct;
import javax.jms.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Component
public class ConsumerMovie {

    private final MoviesRepository moviesRepository;
    private final ObjectMapper objectMapper;
    private final JmsTemplate jmsTemplate;

    private static final Logger logger = LoggerFactory.getLogger(ConsumerMovie.class);

    @Autowired
    public ConsumerMovie(MoviesRepository moviesRepository, ObjectMapper objectMapper, JmsTemplate jmsTemplate) {
        this.moviesRepository = moviesRepository;
        this.objectMapper = objectMapper;
        this.jmsTemplate = jmsTemplate;
    }

    @PostConstruct
    public void clearQueueOnStartup() {
        ConnectionFactory connectionFactory = jmsTemplate.getConnectionFactory();
        try (Connection connection = connectionFactory.createConnection();
             Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {

            // Важно: запускаем соединение!
            connection.start();

            Queue queue = session.createQueue("movie.queue");
            try (MessageConsumer consumer = session.createConsumer(queue)) {
                int count = 0;
                Message message;
                while ((message = consumer.receiveNoWait()) != null) {
                    count++;
                }
                logger.info("Очищено {} сообщений из очереди при запуске", count);
            }
        } catch (JMSException e) {
            logger.error("Ошибка при очистке очереди: {}", e.getMessage());
        }
    }

    @JmsListener(destination = "movie.queue")
   @Transactional
    public void receiveMessage(String jsonMessage) throws JsonProcessingException {
        List<Movie> movieList = objectMapper.readValue(jsonMessage, new TypeReference<List<Movie>>() {
        });

       Set<Integer> filmIdList = moviesRepository.findAllFilmId();

        List<Movie> result = movieList.stream()
                .filter(movie -> !filmIdList.contains(movie.getFilmId()))
                .collect(Collectors.toList());

        moviesRepository.saveAll(result);
    }
}
