package pl.akulau.testkafkastreams.controller;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class KafkaProducerController {

    final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${input.topic.name}")
    String targetTopic;

    @GetMapping("/send")
    public CompletableFuture<Boolean> sendMessage(@RequestParam String message) {
        return kafkaTemplate.send(targetTopic, UUID.randomUUID().toString(), message)
                .handle((mes, t) -> Objects.isNull(t));
    }
}
