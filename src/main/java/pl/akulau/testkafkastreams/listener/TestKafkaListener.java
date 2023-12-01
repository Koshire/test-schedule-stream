package pl.akulau.testkafkastreams.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestKafkaListener {

    @KafkaListener(topics = "${output.topic.name}",
            groupId = "test_consumer",
            batch = "1")
    public void kafkaListener(String message) {
        log.info("MESSAGE RECIEVED: {}", message);
    }
}
