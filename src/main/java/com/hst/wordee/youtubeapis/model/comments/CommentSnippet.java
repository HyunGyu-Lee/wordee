package com.hst.wordee.youtubeapis.model.comments;

import lombok.Data;

/**
 * @author dlgusrb0808@gmail.com
 */
@Data
public class CommentSnippet {
	private String videoId;
	private TopLevelComment topLevelComment;
	private boolean canReply;
	private long totalReplayCount;
	private boolean isPublic;
}
