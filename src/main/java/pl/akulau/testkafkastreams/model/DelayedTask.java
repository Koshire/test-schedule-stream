package pl.akulau.testkafkastreams.model;

import java.time.Instant;

public record DelayedTask<V>(V message, Instant expire) {
}
