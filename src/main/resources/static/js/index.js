$(document).ready(() => {
	// 분석 버튼 클릭
	$('#analysisButton').on('click', async () => {
		console.log('https://www.youtube.com/watch?v=zCamAKs5D3U')
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
	const nounList = Object.keys(nounMap).map(key => ({key: key, count: nounMap[key]}));

	nounList
		.filter(e => e.count > 1)
		.forEach(data => {
				$resultTable.append(`<tr><td class="text-center">${data.key}</td><td class="text-center">${data.count}</td></tr>`);
		})
}