package com.hst.wordee.youtubeapis.model;

import lombok.Data;

/**
 * @author dlgusrb0808@gmail.com
 */
@Data
public class TopLevelComment extends ModelBase {
	private String id;
	private Contents snippet;

	@Data
	public static class Contents {
		private String videoId;
		private String textDisplay;
		private String textOriginal;
		private String authorDisplayName;
		private String authorProfileImageUrl;
		private String authorChannelUrl;
		private AuthorChannel authorChannelId;
		private boolean canRate;
		private String viewerRating;
		private long likeCount;
		private String publishedAt;
		private String updatedAt;
	}

	@Data
	public static class AuthorChannel {
		private String value;
	}
}
