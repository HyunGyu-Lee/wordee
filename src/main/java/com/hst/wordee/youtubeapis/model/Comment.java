package com.hst.wordee.youtubeapis.model;

import lombok.Data;

/**
 * @author dlgusrb0808@gmail.com
 */
@Data
public class Comment extends ModelBase {
	private String id;
	private CommentSnippet snippet;
}
