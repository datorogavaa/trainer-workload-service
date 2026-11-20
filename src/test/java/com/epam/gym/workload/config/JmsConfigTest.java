package com.epam.gym.workload.config;

import org.junit.jupiter.api.Test;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class JmsConfigTest {

    private final JmsConfig config = new JmsConfig();
    private final MessageConverter converter = config.jacksonJmsMessageConverter();

    @Test
    void createsJacksonTextMessageConverter() {
        MappingJackson2MessageConverter jacksonConverter =
                assertInstanceOf(MappingJackson2MessageConverter.class, converter);
        assertInstanceOf(MessageConverter.class, converter);
    }
}
