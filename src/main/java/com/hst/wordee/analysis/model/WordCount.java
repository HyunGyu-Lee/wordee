package com.hst.wordee.analysis.model;

import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * @author hyungyu.lee@nhn.com
 */
@Getter
public class WordCount {
	private String word;
	private Long count;

	public static WordCount of(String word, Long count) {
		WordCount wordCount = new WordCount();
		wordCount.word = word;
		wordCount.count = count;
		return wordCount;
	}
}
