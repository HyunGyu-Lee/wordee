package com.hst.wordee.analysis.ui;

import com.hst.wordee.analysis.service.AnalysisService;
import com.hst.wordee.youtubeapis.service.YoutubeApiService;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
	public ResponseEntity<Map<String, Long>> analysisVideoComments(@PathVariable String videoId) {
		return ResponseEntity.ok(youtubeApiService.getAllCommentThreads(videoId));
	}

}
