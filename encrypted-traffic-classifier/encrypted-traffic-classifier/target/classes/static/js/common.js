function errorRedirect() {
    top.location.href = '/error';
}

function indexRedirect() {
    top.location.href = '/index';
}

function loginRedirect() {
    top.location.href = '/login';
}

layui.use(['util'], function () {
    var util = layui.util;

    util.fixbar({
        bgcolor: "#1E9FFF",
        css: {right: 20, bottom: 50},
        showHeight: 100
    });

});