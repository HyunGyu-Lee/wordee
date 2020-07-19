$(document).ready(() => {
	// 분석 버튼 클릭
	$('#analysisButton').on('click', async () => {
		const videoId = getVideoId($('#videoUrl').val());

		settingVideoDetail(videoId);

		processAnalyze(videoId);
	});
})

function clearResultArea() {
	const $resultTable = $('#resultTable');
	$resultTable.find('tbody').empty();
	return $resultTable;
}

function getVideoId(url) {
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
	videoDetail.tags.forEach(tag => {
		$videoInfoAreaBody.append(`<span class="badge badge-primary">${tag}</span> `);
	})
	$videoInfoArea.append($videoInfoAreaBody);
	$videoInfoArea.parent().parent().show();
}

async function processAnalyze(videoId) {
	const $resultTable = clearResultArea();

	const response = await analysisService.analysisYoutubeComment(videoId);
	const nounMap = response.data;
	const nounList = Object.keys(nounMap).map(key => {
		return {key: key, count: nounMap[key]}
	});

	drawWordCloudChart(nounList);
}

function createData(nounMap, key) {

}

function drawWordCloudChart(data) {
	console.log(data)

	var chart = am4core.create("wordCloud", am4plugins_wordCloud.WordCloud);
	chart.fontFamily = "Courier New";
	var series = chart.series.push(new am4plugins_wordCloud.WordCloudSeries());
	series.randomness = 0.1;
	series.rotationThreshold = 0.5;

	series.data = data.sort((a, b) => b.count - a.count).slice(0, 300);

	series.dataFields.word = "key";
	series.dataFields.value = "count";

	series.heatRules.push({
		"target": series.labels.template,
		"property": "fill",
		"min": am4core.color("#0000CC"),
		"max": am4core.color("#CC00CC"),
		"dataField": "value"
	});

	series.labels.template.url = "https://stackoverflow.com/questions/tagged/{word}";
	series.labels.template.urlTarget = "_blank";
	series.labels.template.tooltipText = "{word}: {value}";

	var hoverState = series.labels.template.states.create("hover");
	hoverState.properties.fill = am4core.color("#FF0000");
}