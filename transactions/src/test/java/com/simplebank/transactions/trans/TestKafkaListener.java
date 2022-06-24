package com.simplebank.transactions.trans;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

@SpringBootTest
@EmbeddedKafka(partitions = 1,
    topics = {})
public class TestKafkaListener {


}
