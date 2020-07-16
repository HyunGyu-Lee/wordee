package com.hst.wordee.youtubeapis.service;

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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author dlgusrb0808@gmail.com
 */
@Service
public class YoutubeApiService {

	private static final int PAGE_SIZE = 100;

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
	 * {videoId}의 모든 댓글 스레드 조회
	 * @param videoId 비디오 ID
	 * @return 댓글 스레드
	 */
	public Map<String, Long> getAllCommentThreads(String videoId) {
		Map<String, Long> nounMap = new HashMap<>();
		String nextPageToken = null;

		do {
			CommentThreadsResponse commentThreads = getCommentThreads(videoId, 50, nextPageToken);
			for (Comment comment : commentThreads.getItems()) {
				TopLevelComment.Contents contents = comment.getSnippet().getTopLevelComment().getSnippet();
				try {
					KomoranResult result = analysisService.analyze(contents.getTextDisplay());
					nounMap.putAll(result.getNouns().stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting())));
				} catch (Exception e) {
					logger.warn("분석불가: {}, errorMessage: {}", contents.getTextDisplay(), e.getMessage());
				}
			}
			nextPageToken = commentThreads.getNextPageToken();
		} while (nextPageToken != null);
		return nounMap;
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
