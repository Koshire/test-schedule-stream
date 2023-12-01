package pl.akulau.testkafkastreams.config.serde;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import pl.akulau.testkafkastreams.model.DelayedTask;

public class DelayedTaskSerde<V> implements Serde<DelayedTask<V>> {

    @Override
    public Serializer<DelayedTask<V>> serializer() {
        return new JsonSerializer<>();
    }

    @Override
    public Deserializer<DelayedTask<V>> deserializer() {
        return new JsonDeserializer<>(DelayedTask.class);
    }
}
