/*
 * Copyright 2013-2017 Simba Open Source
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

// Jasmine-Ajax interface
var ajaxRequests = [];

function mostRecentAjaxRequest() {
  if (ajaxRequests.length > 0) {
    return ajaxRequests[ajaxRequests.length - 1];
  } else {
    return null;
  }
}

function clearAjaxRequests() {
  ajaxRequests = [];
}

// Fake XHR for mocking Ajax Requests & Responses
function FakeXMLHttpRequest() {
  var extend = Object.extend || jQuery.extend;
  extend(this, {
    requestHeaders: {},

    open: function() {
      this.method = arguments[0];
      this.url = arguments[1];
      this.username = arguments[3];
      this.password = arguments[4];
      this.readyState = 1;
    },

    setRequestHeader: function(header, value) {
      this.requestHeaders[header] = value;
    },

    abort: function() {
      this.readyState = 0;
    },

    readyState: 0,

    onload: function() {
    },

    onreadystatechange: function(isTimeout) {
    },

    status: null,

    send: function(data) {
      this.params = data;
      this.readyState = 2;
    },

    data: function() {
      var data = {};
      if (typeof this.params !== 'string') return data;
      var params = this.params.split('&');

      for (var i = 0; i < params.length; ++i) {
        var kv = params[i].replace(/\+/g, ' ').split('=');
        var key = decodeURIComponent(kv[0]);
        data[key] = data[key] || [];
        data[key].push(decodeURIComponent(kv[1]));
        data[key].sort();
      }
      return data;
    },

    getResponseHeader: function(name) {
      return this.responseHeaders[name];
    },

    getAllResponseHeaders: function() {
      var responseHeaders = [];
      for (var i in this.responseHeaders) {
        if (this.responseHeaders.hasOwnProperty(i)) {
          responseHeaders.push(i + ': ' + this.responseHeaders[i]);
        }
      }
      return responseHeaders.join('\r\n');
    },

    responseText: null,

    response: function(response) {
      this.status = response.status;
      this.responseText = response.responseText || "";
      this.readyState = 4;
      this.responseHeaders = response.responseHeaders ||
      {"Content-type": response.contentType || "application/json" };
      // uncomment for jquery 1.3.x support
      // jasmine.Clock.tick(20);

      this.onload();
      this.onreadystatechange();
    },
    responseTimeout: function() {
      this.readyState = 4;
      jasmine.Clock.tick(jQuery.ajaxSettings.timeout || 30000);
      this.onreadystatechange('timeout');
    }
  });

  return this;
}


jasmine.Ajax = {

  isInstalled: function() {
    return jasmine.Ajax.installed === true;
  },

  assertInstalled: function() {
    if (!jasmine.Ajax.isInstalled()) {
      throw new Error("Mock ajax is not installed, use jasmine.Ajax.useMock()");
    }
  },

  useMock: function() {
    if (!jasmine.Ajax.isInstalled()) {
      var spec = jasmine.getEnv().currentSpec;
      spec.after(jasmine.Ajax.uninstallMock);

      jasmine.Ajax.installMock();
    }
  },

  installMock: function() {
    if (typeof jQuery != 'undefined') {
      jasmine.Ajax.installJquery();
    } else {
      throw new Error("jasmine.Ajax currently only supports jQuery");
    }
    jasmine.Ajax.installed = true;
  },

  installJquery: function() {
    jasmine.Ajax.mode = 'jQuery';
    jasmine.Ajax.real = jQuery.ajaxSettings.xhr;
    jQuery.ajaxSettings.xhr = jasmine.Ajax.jQueryMock;

  },

  uninstallMock: function() {
    jasmine.Ajax.assertInstalled();
    if (jasmine.Ajax.mode == 'jQuery') {
      jQuery.ajaxSettings.xhr = jasmine.Ajax.real;
    }
    jasmine.Ajax.reset();
  },

  reset: function() {
    jasmine.Ajax.installed = false;
    jasmine.Ajax.mode = null;
    jasmine.Ajax.real = null;
  },

  jQueryMock: function() {
    var newXhr = new FakeXMLHttpRequest();
    ajaxRequests.push(newXhr);
    return newXhr;
  },

  installed: false,
  mode: null
};
