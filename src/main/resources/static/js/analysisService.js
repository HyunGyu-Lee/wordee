(function (root, factory) {
	if (typeof define === 'function' && define.amd) {
		define([], factory);
	} else if (typeof module === 'object' && module.exports) {
		module.exports = factory();
	} else {
		root['analysisService'] = factory();
	}
}(this, function () {
	return {
		getYoutubeVideoInfo(videoId) {
			return axios.get(`/api/analysis/youtube-search/${videoId}`);
		},
		analysisYoutubeComment(videoId, maxCommentCount) {
			const param = {
				params: {
					maxCommentCount: maxCommentCount
				}
			}
			return axios.get(`/api/analysis/youtube-comment-analysis/${videoId}`, param);
		}
	};
}));