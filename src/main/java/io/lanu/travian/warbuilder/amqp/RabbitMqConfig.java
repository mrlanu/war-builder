package io.lanu.travian.warbuilder.amqp;

import io.lanu.travian.warbuilder.models.CommandMessage;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMqConfig {

    // config for RPC
    @Bean(name = "my.rpc.direct")
    public DirectExchange directExchangeRPC() {
        return new DirectExchange("my.rpc.direct");
    }

    @Bean(name = "my.rpc.queue")
    public Queue queue() {
        return new Queue("rpc.requests");
    }

    @Bean(name = "my.rpc.binding")
    public Binding bindingRPC(@Qualifier("my.rpc.direct") DirectExchange exchange,
                              @Qualifier("my.rpc.queue") Queue queue) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with("rpc");
    }


    // config for direct
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange("my.fanout");
    }

    @Bean(name = "autoDeleteQueue")
    public Queue autoDeleteQueue() {
        return new AnonymousQueue();
    }

    @Bean(name = "binding")
    public Binding binding(FanoutExchange fanoutExchange,
                           @Qualifier("autoDeleteQueue")Queue autoDeleteQueue) {
        return BindingBuilder.bind(autoDeleteQueue).to(fanoutExchange);
    }

    @Bean(name = "rabbitTemplate")
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory)
    {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        template.setReplyTimeout(20000);
        return template;
    }

    @Bean
    public MessageConverter messageConverter()
    {
        Jackson2JsonMessageConverter jsonMessageConverter = new Jackson2JsonMessageConverter();
        jsonMessageConverter.setClassMapper(classMapper());
        return jsonMessageConverter;
    }

    @Bean
    public DefaultClassMapper classMapper()
    {
        DefaultClassMapper classMapper = new DefaultClassMapper();
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("io.lanu.travian.warbuildercommander.models.CommandMessage", CommandMessage.class);
        classMapper.setIdClassMapping(idClassMapping);
        return classMapper;
    }
}
