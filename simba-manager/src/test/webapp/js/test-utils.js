var promiseWithDone = function(data) {
	var promise = new jQuery.Deferred();
	promise.resolve(data);
	return promise;
};

var promiseWithReject = function() {
	var promise = new jQuery.Deferred();
	promise.reject();
	return promise;
};