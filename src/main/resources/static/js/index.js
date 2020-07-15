$(document).ready(() => {
	// 분석 버튼 클릭
	$('#analysisButton').on('click', async () => {
		const videoId = getVideoId($('#videoUrl').val());
		const response = await analysisService.youtubeCommentAnalysis(videoId);
		const nounMap = response.data;
		const nounList = Object.keys(nounMap).map(key => ({key: key, count: nounMap[key]}));

		const $resultTable = $('#resultTable');
		$resultTable.find('tbody').empty();
		nounList
			.filter(e => e.count > 1)
			.forEach(data => {
				$resultTable.append(`<tr><td class="text-center">${data.key}</td><td class="text-center">${data.count}</td></tr>`);
			})
	});
})

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