package org.example.VKR.dto.Kinopoisk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KinopoiskItem {
    @JsonProperty("items")
    private List<Kinopoisk> items;

    public List<Kinopoisk> getItems() {
        return items;
    }

    public void setItems(List<Kinopoisk> items) {
        this.items = items;
    }
}
