package com.hst.wordee.youtubeapis.model.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hst.wordee.youtubeapis.model.ModelBase;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * @author dlgusrb0808@gmail.com
 */
@Data
@ToString
public class VideoDetail extends ModelBase {
    private String id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private List<String> tags;

    @SuppressWarnings("unchecked")
    @JsonProperty("items")
    private void mappingSnippet(List<Object> items) {
        Map<String, Object> item = (Map<String, Object>) items.get(0);

        this.id = (String) item.get("id");

        Map<String, Object> snippet = (Map<String, Object>) item.get("snippet");
        this.title = (String) snippet.get("title");
        this.description = (String) snippet.get("description");

        Map<String, Object> thumbnails = (Map<String, Object>) snippet.get("thumbnails");
        Map<String, Object> defaultThumbnail = (Map<String, Object>) thumbnails.get("high");
        this.thumbnailUrl = (String) defaultThumbnail.get("url");
        this.tags = (List<String>) snippet.get("tags");
    }
}

