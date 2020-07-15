package com.hst.wordee.analysis.service;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import org.springframework.stereotype.Service;

/**
 * @author dlgusrb0808@gmail.com
 */
@Service
public class AnalysisService {
	/**
	 * Komoran API Instance
	 */
	private final Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);

	/**
	 * 형태소 분석
	 * @param sentence 분석 문장
	 * @return 분석 결과
	 */
	public KomoranResult analyze(String sentence) {
		return komoran.analyze(sentence);
	}

}
