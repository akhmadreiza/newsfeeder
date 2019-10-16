package com.ara27.newsfeeder.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"size", "data"})
public class Data {

    @JsonProperty("data")
    private List data;

    @JsonProperty("size")
    private int count;

    public Data(List data) {
        this.data = data;
        this.count = data.size();
    }

    public Data(List data, int count) {
        this.data = data;
        this.count = count;
    }

    public List getData() {
        return data;
    }

    public void setData(List data) {
        this.data = data;
    }
}
