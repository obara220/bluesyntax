package com.panda.aoodds.esport.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
public class MongoDBConfig {

    @Value("${spring.data.mongodb.ao.producer.uri}")
    private String MONGO_PRODUCER_URI;
    @Value("${spring.data.mongodb.ao.consumer.uri}")
    private String MONGO_CONSUMER_URI;

    @Bean(name = "producerMongoTemplate")
    public MongoTemplate getProducerMongoMongo() {
        return new MongoTemplate(new SimpleMongoClientDatabaseFactory(MONGO_PRODUCER_URI));
    }

    @Bean(name = "consumerMongoTemplate")
    public MongoTemplate getConsumerMongoMongo() {
        return new MongoTemplate(new SimpleMongoClientDatabaseFactory(MONGO_CONSUMER_URI));
    }
}
