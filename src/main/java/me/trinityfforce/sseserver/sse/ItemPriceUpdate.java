package me.trinityfforce.sseserver.sse;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class ItemPriceUpdate {

    private final Long itemId;
    private final Integer price;

    @JsonCreator
    public ItemPriceUpdate(@JsonProperty("itemId") Long itemId,
                           @JsonProperty("price") Integer price) {
        this.itemId = itemId;
        this.price = price;
    }

}
