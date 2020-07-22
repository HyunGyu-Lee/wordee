package com.hst.wordee.analysis.ui;

import com.hst.wordee.analysis.model.AnalysisResponse;
import com.hst.wordee.analysis.model.WordCount;
import com.hst.wordee.analysis.service.AnalysisService;
import com.hst.wordee.youtubeapis.model.search.VideoDetail;
import com.hst.wordee.youtubeapis.service.YoutubeApiService;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author dlgusrb0808@gmail.com
 */
@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {

	private final AnalysisService analysisService;
	private final YoutubeApiService youtubeApiService;

	@GetMapping("/sentence-analysis")
	public ResponseEntity<KomoranResult> test(@RequestParam String sentence) {
		return ResponseEntity.ok(analysisService.analyze(sentence));
	}

	@GetMapping("youtube-comment-analysis/{videoId}")
	public ResponseEntity<AnalysisResponse> analysisVideoComments(@PathVariable String videoId,
																  @RequestParam int maxCommentCount) {
		return ResponseEntity.ok(youtubeApiService.analysisYoutubeComments(videoId, maxCommentCount));
	}

	@GetMapping("youtube-search/{videoId}")
	public ResponseEntity<VideoDetail> getVideoTest(@PathVariable String videoId) {
		return ResponseEntity.ok(youtubeApiService.getVideoDetail(videoId));
	}


}
