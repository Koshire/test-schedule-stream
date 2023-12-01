package pl.akulau.testkafkastreams.supplier;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import pl.akulau.testkafkastreams.model.DelayedTask;

public class DelayedTaskProcessor<K, V> implements Processor<K, V, K, V> {
    private KeyValueStore<K, DelayedTask<V>> store;
    private ProcessorContext<K, V> context;

    private final String storeName;

    public DelayedTaskProcessor(String storeName) {
        this.storeName = storeName;
    }

    @Override
    public void init(ProcessorContext<K, V> context) {
        this.context = context;
        store = this.context.getStateStore(storeName);
        this.context.schedule(Duration.ofSeconds(1), PunctuationType.WALL_CLOCK_TIME, this::wallClockTimePunctuator);
    }

    @Override
    public void process(Record<K, V> record) {
        Optional.ofNullable(record)
                .ifPresent(rec -> {
                    store.put(rec.key(), new DelayedTask<V>(rec.value(),
                            Instant.now().plusSeconds(10)));
                    context.forward(rec);
                });
    }

    @Override
    public void close() {
        Processor.super.close();
    }

    public void wallClockTimePunctuator(Long timeStamp) {
        Instant instant = Instant.ofEpochMilli(timeStamp);
        try (KeyValueIterator<K, DelayedTask<V>> iterator = store.all()) {
            while (iterator.hasNext()) {
                KeyValue<K, DelayedTask<V>> keyValue = iterator.next();
                if (keyValue.value.expire().isBefore(instant)) {
                    context.forward(new Record<>(
                            keyValue.key,
                            (V) (keyValue.value.message() + "DELAYED"), timeStamp)
                    );
                    store.delete(keyValue.key);
                }
            }
        }
    }
}
