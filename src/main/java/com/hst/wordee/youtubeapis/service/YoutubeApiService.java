package com.hst.wordee.youtubeapis.service;

import com.hst.wordee.analysis.model.AnalysisResponse;
import com.hst.wordee.analysis.model.WordCount;
import com.hst.wordee.analysis.service.AnalysisService;
import com.hst.wordee.youtubeapis.exception.YoutubeApiException;
import com.hst.wordee.youtubeapis.model.comments.Comment;
import com.hst.wordee.youtubeapis.model.comments.CommentThreadsResponse;
import com.hst.wordee.youtubeapis.model.comments.TopLevelComment;
import com.hst.wordee.youtubeapis.model.search.VideoDetail;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author dlgusrb0808@gmail.com
 */
@Service
public class YoutubeApiService {

	private static final int DEFAULT_FETCH_SIZE = 100;
	private static final int DEFAULT_MAX_COMMENT_SIZE = 1000;

	private static final Logger logger = LoggerFactory.getLogger(YoutubeApiService.class);

	@Value("${app.youtube-api.comment-api}")
	private String commentApiUrl;

	@Value("${app.youtube-api.search-api}")
	private String searchApiUrl;

	@Value("${app.youtube-api.app-key}")
	private String appKey;

	private final RestTemplate restTemplate;
	private final AnalysisService analysisService;

	public YoutubeApiService(RestTemplate restTemplate, AnalysisService analysisService) {
		this.restTemplate = restTemplate;
		this.analysisService = analysisService;
	}

	/**
	 * 유튜브 기본 정보 조회
	 * @param videoId 비디오 ID
	 * @return 유튜브 기본 정보
	 */
	public VideoDetail getVideoDetail(String videoId) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(searchApiUrl)
				.queryParam("textFormat", "plainText")
				.queryParam("part", "snippet")
				.queryParam("key", appKey)
				.queryParam("id", videoId);

		ResponseEntity<VideoDetail> response = restTemplate.exchange(builder.build().toUriString(), HttpMethod.GET,
				null, VideoDetail.class);

		if (response.getStatusCode() != HttpStatus.OK) {
			throw new YoutubeApiException("영상 조회 실패. " + response.getStatusCode().getReasonPhrase());
		}

		return response.getBody();
	}

	/**
	 * {videoId}의 영상 댓글 분석
	 * @param videoId 비디오 ID
	 * @param maxCommentCount 분석 댓글 수
	 * @return 댓글 스레드
	 */
	public AnalysisResponse analysisYoutubeComments(String videoId, int maxCommentCount) {
		Map<String, Long> wordCountMap = new HashMap<>();
		String nextPageToken = null;
		int processCommentCount = 0;
		maxCommentCount = maxCommentCount > 0 ? maxCommentCount : DEFAULT_MAX_COMMENT_SIZE;

		while (processCommentCount == 0 || nextPageToken != null) {
			CommentThreadsResponse commentThreads = getCommentThreads(videoId, DEFAULT_FETCH_SIZE, nextPageToken);
			for (Comment comment : commentThreads.getItems()) {
				TopLevelComment.Contents contents = comment.getSnippet().getTopLevelComment().getSnippet();
				wordCountMap.putAll(extractWordCount(contents.getTextDisplay()));
				processCommentCount++;

				if (processCommentCount >= maxCommentCount) {
					logger.info("{} 영상 댓글 {}개 분석 완료..", videoId, processCommentCount);
					return AnalysisResponse.of(processCommentCount, convertWordCountMapToList(wordCountMap));
				}
			}
			logger.info("{} 영상 댓글 {}개 분석 완료..", videoId, processCommentCount);
			nextPageToken = commentThreads.getNextPageToken();
		}
		return AnalysisResponse.of(processCommentCount, convertWordCountMapToList(wordCountMap));
	}

	// WordCountMap -> List 변환
	private List<WordCount> convertWordCountMapToList(Map<String, Long> wordCountMap) {
		return wordCountMap.entrySet().stream()
				.map(e -> WordCount.of(e.getKey(), e.getValue()))
				.sorted((a, b) -> (int) (b.getCount() - a.getCount()))
				.collect(Collectors.toList());
	}

	// 분석 서비스 call + 명사 단어 갯수 추출
	private Map<String, Long> extractWordCount(String sentence) {
		try {
			KomoranResult result = analysisService.analyze(sentence);
			return result.getNouns().stream()
					.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		} catch (Exception e) {
			logger.warn("분석불가: {}, errorMessage: {}", sentence, e.getMessage());
			return Collections.emptyMap();
		}
	}

	/***
	 * 댓글 스레드 조회 API 호출
	 * @param videoId 비디오 ID
	 * @param size 페이지 크기
	 * @param pageToken 페이지 토큰
	 * @return 댓글 스레드
	 */
	public CommentThreadsResponse getCommentThreads(String videoId, int size, String pageToken) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(commentApiUrl)
				.queryParam("textFormat", "plainText")
				.queryParam("part", "snippet")
				.queryParam("key", appKey)
				.queryParam("videoId", videoId)
				.queryParam("maxResults", size);

		if (StringUtils.isNotEmpty(pageToken)) {
			builder.queryParam("pageToken", pageToken);
		}

		ResponseEntity<CommentThreadsResponse> response = restTemplate.exchange(builder.build().toUriString(), HttpMethod.GET,
				null, CommentThreadsResponse.class);

		if (response.getStatusCode() != HttpStatus.OK) {
			throw new YoutubeApiException("댓글 조회 실패. " + response.getStatusCode().getReasonPhrase());
		}
		return response.getBody();
	}

}
