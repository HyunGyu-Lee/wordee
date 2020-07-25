(function (root, factory) {
	if (typeof define === 'function' && define.amd) {
		define([], factory);
	} else if (typeof module === 'object' && module.exports) {
		module.exports = factory();
	} else {
		root['Store'] = factory();
	}
}(this, function () {
	const object = {};
	return {
		put: (name, value) => {
			object[name] = value;
		},
		get: (name) => {
			return object[name];
		}
	};
}));