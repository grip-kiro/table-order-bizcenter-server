package com.tableorder.admin.sse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class SseService {

    private final Map<Long, CopyOnWriteArrayList<SseEmitter>> adminEmitters = new ConcurrentHashMap<>();

    public SseEmitter createAdminEmitter(Long storeId) {
        SseEmitter emitter = new SseEmitter(0L); // no timeout

        adminEmitters.computeIfAbsent(storeId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(storeId, emitter));
        emitter.onTimeout(() -> removeEmitter(storeId, emitter));
        emitter.onError(e -> removeEmitter(storeId, emitter));

        // 초기 연결 확인 이벤트
        try {
            emitter.send(SseEmitter.event().name("CONNECTED").data("connected"));
        } catch (IOException e) {
            removeEmitter(storeId, emitter);
        }

        return emitter;
    }

    public void notifyOrderCreated(Long storeId, Object orderData) {
        CopyOnWriteArrayList<SseEmitter> emitters = adminEmitters.get(storeId);
        if (emitters == null || emitters.isEmpty()) return;

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("ORDER_CREATED")
                        .data(orderData));
            } catch (IOException e) {
                removeEmitter(storeId, emitter);
            }
        }
    }

    private void removeEmitter(Long storeId, SseEmitter emitter) {
        CopyOnWriteArrayList<SseEmitter> emitters = adminEmitters.get(storeId);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                adminEmitters.remove(storeId);
            }
        }
    }
}
