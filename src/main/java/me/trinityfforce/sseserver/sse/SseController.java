package me.trinityfforce.sseserver.sse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/item")
public class SseController {

    //private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();
//    @GetMapping(path = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public Flux<ServerSentEvent<String>> streamEvents() {
//        return Flux.interval(Duration.ofSeconds(1))
//            .map(sequence -> ServerSentEvent.<String>builder()
//                .id(String.valueOf(sequence))
//                .event("message")
//                .data("Hello from server: " + sequence)
//                .build());
//    }

    @GetMapping("/subscribe/{itemId}")
    public SseEmitter subscribe(@PathVariable Long itemId) {
        // 새로운 SseEmitter 생성
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

//        emitter.onCompletion(()->{
//            synchronized (this.emitters){
//                this.emitters.remove(emitter);
//            }
//        });

        // 맵에 저장
        //emitters.put(itemId, emitter);

        // 연결 종료 시 맵에서 제거
//        emitter.onCompletion(() -> emitters.remove(itemId));
//        emitter.onTimeout(() -> emitters.remove(itemId));
//        emitter.onError(e -> emitters.remove(itemId));

        emitter.onCompletion(() -> removeSubscriber(itemId, emitter));
        emitter.onTimeout(() -> removeSubscriber(itemId, emitter));
        emitter.onError((e) -> removeSubscriber(itemId, emitter));

        emitters.computeIfAbsent(itemId, k -> new ArrayList<>()).add(emitter);
//        emitters.computeIfAbsent(itemId, k -> Collections.synchronizedList(new ArrayList<>())).add(emitter); // synchronizedList 사용

        return emitter;
    }

    public void sendItemUpdate(Long itemId, Object data) {
        List<SseEmitter> subscribers = emitters.getOrDefault(itemId, Collections.emptyList());

//        if (subscribers != null) {
//            subscribers.forEach(s -> {
//                try {
//                    if (s != null) {
//                        s.send(SseEmitter.event().name("itemUpdate").data(data).build());
//                    }
//                } catch (IOException e) {
//                    s.completeWithError(e);
//                }
//            });
//        }
        for (SseEmitter s : new ArrayList<>(subscribers)) {
            try {
                s.send(SseEmitter.event().name("itemUpdate").data(data).build());
            } catch (IOException e) {
                removeSubscriber(itemId, s);
                s.completeWithError(e);
            }
        }
    }

    private void removeSubscriber(Long room, SseEmitter emitter) {
        List<SseEmitter> subscribers = emitters.get(room);
        if (subscribers != null) {
            subscribers.remove(emitter);
            if (subscribers.isEmpty()) { // 구독자만 지우는게 아니라 emitters 까지 같이 삭제
                emitters.remove(room);
            }
        }
    }
}
