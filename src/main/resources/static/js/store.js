(function (root, factory) {
	if (typeof define === 'function' && define.amd) {
		define([], factory);
	} else if (typeof module === 'object' && module.exports) {
		module.exports = factory();
	} else {
		root['store'] = factory();
	}
}(this, function () {
	return {
		data: {},
		component: {},
		setData: (name, value) => {
			this.data[name] = value;
		},
		setComponent: (name, component) => {
			this.component[name] = component;
		},
		getData: (name) => {
			return this.data[name];
		},
		getComponent: (name) => {
			return this.component[name];
		}
	};
}));