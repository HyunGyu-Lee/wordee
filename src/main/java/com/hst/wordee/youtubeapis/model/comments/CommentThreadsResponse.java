package com.hst.wordee.youtubeapis.model.comments;

import com.hst.wordee.youtubeapis.model.ModelBase;
import com.hst.wordee.youtubeapis.model.PageInfo;
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
