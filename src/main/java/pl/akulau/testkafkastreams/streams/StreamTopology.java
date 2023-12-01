package pl.akulau.testkafkastreams.streams;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.akulau.testkafkastreams.config.serde.DelayedTaskSerde;
import pl.akulau.testkafkastreams.model.DelayedTask;
import pl.akulau.testkafkastreams.supplier.DelayedTaskProcessor;

@Slf4j
@Component
public class StreamTopology {

    @Value("${input.topic.name}")
    private String inputTopic;

    @Value("${output.topic.name}")
    private String outputTopic;

    private static final String KV_STORE_NAME = "KV_STORE";

    @Autowired
    public void get(StreamsBuilder streamBuilder) {
        StoreBuilder<KeyValueStore<String, DelayedTask<String>>> kvStore =
                Stores.keyValueStoreBuilder(Stores.persistentKeyValueStore(KV_STORE_NAME),
                        Serdes.String(),
                        new DelayedTaskSerde<>());

        streamBuilder
                .addStateStore(kvStore)
                .stream(inputTopic, Consumed.with(Serdes.String(), Serdes.String()))
                .process(() -> new DelayedTaskProcessor<>(KV_STORE_NAME), KV_STORE_NAME)
                .map((s, s2) -> {
                    KeyValue<String, String> pair = KeyValue.pair(s, s2 + " MAPPED_VALUE");
                    log.info("{}", pair);
                    return pair;
                })
                .to(outputTopic, Produced.with(Serdes.String(), Serdes.String()));
    }
}