package com.hst.wordee.youtubeapis.model;

import lombok.Data;

import java.util.List;

/**
 * @author dlgusrb0808@gmail.com
 */
@Data
public class CommentThreadsResponse extends ModelBase {
	private String nextPageToken;
	private PageInfo pageInfo;
	private List<Comment> items;
}
