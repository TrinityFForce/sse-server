package me.trinityfforce.sseserver.sse;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
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
