package me.trinityfforce.sseserver.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.trinityfforce.sseserver.sse.ItemPriceUpdate;
import me.trinityfforce.sseserver.sse.SseController;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final SseController sseController;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 메시지에서 데이터 추출
        String messageBody = new String(message.getBody());
        ItemPriceUpdate update = convertMessageBodyToItemPriceUpdate(messageBody);

        // SSE 컨트롤러를 통해 연결된 클라이언트에게 메시지 전송
        sseController.sendItemUpdate(update.getItemId(), update.getPrice());
    }

    private ItemPriceUpdate convertMessageBodyToItemPriceUpdate(String messageBody) {
        try {
            return mapper.readValue(messageBody, ItemPriceUpdate.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
