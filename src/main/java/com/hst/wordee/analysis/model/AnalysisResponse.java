package com.hst.wordee.analysis.model;

import java.util.List;

/**
 * @author dlgusrb0808@gmail.com
 */
public class AnalysisResponse {
	private long totalCommentCount;
	private long totalWordCount;
	private List<WordCount> wordCounts;

	public long getTotalCommentCount() {
		return totalCommentCount;
	}

	public long getTotalWordCount() {
		return totalWordCount;
	}

	public List<WordCount> getWordCounts() {
		return wordCounts;
	}

	public static AnalysisResponse of(long totalCommentCount, List<WordCount> wordCounts) {
		AnalysisResponse analysisResponse = new AnalysisResponse();
		analysisResponse.totalCommentCount = totalCommentCount;
		analysisResponse.totalWordCount = wordCounts.size();
		analysisResponse.wordCounts = wordCounts;
		return analysisResponse;
	}
}
