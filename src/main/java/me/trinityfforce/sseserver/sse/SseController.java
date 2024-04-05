package me.trinityfforce.sseserver.sse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1/item")
public class SseController {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    @GetMapping("/subscribe/{itemId}")
    public SseEmitter subscribe(@PathVariable Long itemId) {
        // 새로운 SseEmitter 생성
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        // 맵에 저장
        emitters.put(itemId, emitter);

        // 연결 종료 시 맵에서 제거
        emitter.onCompletion(() -> emitters.remove(itemId));
        emitter.onTimeout(() -> emitters.remove(itemId));
        emitter.onError(e -> emitters.remove(itemId));

        return emitter;
    }

    public void sendItemUpdate(Long itemId, Object data) {
        SseEmitter emitter = emitters.get(itemId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("itemUpdate").data(data));
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        }
    }
}
