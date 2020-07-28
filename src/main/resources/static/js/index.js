const Namespaces = {
	DATA: {
		ANALYSIS_DATA: 'analysisData',
	},
	UI: {
		CHART: 'chart',
		SERIES: 'series'
	}
}

$(document).ready(() => {
	Store.put(Namespaces.DATA.ANALYSIS_DATA, []);

	initializeWordCloudChart();

	// 분석 버튼 클릭
	$('#analysisButton').on('click', async () => {
		const videoId = parseVideoId($('#videoUrl').val());
		const $maxWordCountInput = $('#maxWordCount');
		const $maxCommentCountInput = $('#maxCommentCount');

		settingVideoDetail(videoId);

		processAnalyze(videoId, parseInt($maxCommentCountInput.val()), parseInt($maxWordCountInput.val()));

		$maxWordCountInput.parent().parent().parent().show();
	});

	$('#maxWordCount').on('change', function () {
		const maxWordCount = $(this).val();
		drawAnalysisResult(Store.get(Namespaces.DATA.ANALYSIS_DATA), parseInt(maxWordCount));
		$('#maxWordCountView').text(maxWordCount);
	})

	$('#maxCommentCount').on('input', function () {
		const maxCommentCount = $(this).val();
		$('#maxCommentCountView').text(maxCommentCount);
	})
})

function parseVideoId(url) {
	const urlToken = url.split('/');
	const uri = urlToken[urlToken.length - 1];
	if (uri.startsWith('watch')) {
		const params = uri.split('?');
		const paramToken = params[1].split('&');
		for (let param of paramToken) {
			const kv = param.split('=');
			if (kv[0] === 'v') {
				return kv[1];
			}
		}
	}
	return uri;
}

async function settingVideoDetail(videoId) {
	const response = await analysisService.getYoutubeVideoInfo(videoId);
	const videoDetail = response.data;

	var $videoInfoArea = $('#video-info');

	$videoInfoArea.empty();
	$videoInfoArea.append(`<img src="${videoDetail.thumbnailUrl}" class="card-img-top" >`);

	var $videoInfoAreaBody = $('<div class="card-body">');
	$videoInfoAreaBody.append(`<p class="card-text">${videoDetail.title}</p>`);
	if (videoDetail.tags) {
		videoDetail.tags.forEach(tag => {
			$videoInfoAreaBody.append(`<span class="badge badge-primary">${tag}</span> `);
		})
	}
	$videoInfoArea.append($videoInfoAreaBody);

	$('#analysisSummaryArea').show();
	scrollBottom();
}

async function processAnalyze(videoId, maxCommentCount, maxWordCount) {
	const response = await analysisService.analysisYoutubeComment(videoId, maxCommentCount);
	const analysisData = response.data;

	Store.put(Namespaces.DATA.ANALYSIS_DATA, analysisData);

	drawAnalysisResult(Store.get(Namespaces.DATA.ANALYSIS_DATA), maxWordCount);

	$('#analysisResultArea').show();
	scrollBottom();
}

function drawAnalysisResult(data, maxWordCount) {
	const seriesInstance = Store.get(Namespaces.UI.SERIES);
	seriesInstance.data = data.wordCounts.slice(0, maxWordCount);
}

function initializeWordCloudChart() {
	const wordCloudChart = am4core.create("wordCloud", am4plugins_wordCloud.WordCloud);
	wordCloudChart.fontFamily = "Courier New";
	const series = wordCloudChart.series.push(new am4plugins_wordCloud.WordCloudSeries());
	series.dataFields.word = "word";
	series.dataFields.value = "count";

	series.randomness = 0.1;
	series.rotationThreshold = 0.5;

	series.heatRules.push({
		"target": series.labels.template,
		"property": "fill",
		"min": am4core.color("#0000CC"),
		"max": am4core.color("#CC00CC"),
		"dataField": "value"
	});

	series.labels.template.url = `https://www.youtube.com/results?search_query={word}`;
	series.labels.template.urlTarget = "_blank";
	series.labels.template.tooltipText = "{word} ({count})";

	var hoverState = series.labels.template.states.create("hover");
	hoverState.properties.fill = am4core.color("#FF0000");

	Store.put(Namespaces.UI.CHART, wordCloudChart);
	Store.put(Namespaces.UI.SERIES, series);
}

function scrollBottom() {
	// setTimeout(function () {
	// 	$("html, body").animate({ scrollTop: $(document).height() }, 2000);
	// }, 100);
	$("html, body").animate({ scrollTop: $(document).height() }, 2000);
}