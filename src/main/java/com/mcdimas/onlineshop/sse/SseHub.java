package com.mcdimas.onlineshop.sse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class SseHub {
    private final Map<String, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String topic) {
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);
        emitters.computeIfAbsent(topic, ignored -> new CopyOnWriteArrayList<>()).add(emitter);
        emitter.onCompletion(() -> remove(topic, emitter));
        emitter.onTimeout(() -> remove(topic, emitter));
        emitter.onError(ignored -> remove(topic, emitter));
        return emitter;
    }

    public void publish(String topic, Object payload) {
        List<SseEmitter> topicEmitters = emitters.getOrDefault(topic, List.of());
        for (SseEmitter emitter : topicEmitters) {
            try {
                emitter.send(payload);
            } catch (IOException ex) {
                remove(topic, emitter);
            }
        }
    }

    private void remove(String topic, SseEmitter emitter) {
        List<SseEmitter> topicEmitters = emitters.get(topic);
        if (topicEmitters != null) {
            topicEmitters.remove(emitter);
        }
    }
}
