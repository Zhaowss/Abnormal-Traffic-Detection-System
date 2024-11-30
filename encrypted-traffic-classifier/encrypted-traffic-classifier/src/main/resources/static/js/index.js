layui.use(['jquery', 'layer', 'miniAdmin'], function () {
    var $ = layui.jquery,
        layer = layui.layer,
        miniAdmin = layui.miniAdmin;

    var options = {
        iniUrl: "static/layuimini-v2/api/init.json",
        clearUrl: "static/layuimini-v2/api/clear.json",
        urlHashLocation: false,
        bgColorDefault: false,
        multiModule: false,
        menuChildOpen: false,
        leftMenuIsHide: false,
        loadingTime: 0,
        pageAnim: true,
        maxTabNum: 20,
        clickHomeTabRefresh: false
    };
    miniAdmin.render(options);

    $.ajax({
        url: '/user/info',
        type: 'GET',
        data: null,
        dataType: 'json',
        contentType: 'application/json;charset=UTF-8',
        success: function (res) {
            if (res.code === 200) {
                $("#userName").html(res.data.userName);
            } else {
                layer.msg(res.msg, {icon: 2, time: 1500});
            }
        },
        error: function (error) {
            layer.msg(error, {icon: 2, time: 1500}, function () {
                errorRedirect();
            });
        }
    });

    $('.login-out').on("click", function () {
        var index = layer.load(0, {shade: true});
        $.ajax({
            url: '/user/logout',
            type: 'GET',
            data: null,
            dataType: 'json',
            contentType: 'application/json;charset=UTF-8',
            success: function (res) {
                if (res.code === 200) {
                    layer.msg(res.msg, {icon: 1, time: 1500}, function () {
                        loginRedirect();
                    });
                } else {
                    layer.msg(res.msg, {icon: 2, time: 1500});
                }
                layer.close(index);
            },
            error: function (error) {
                layer.msg(error, {icon: 2, time: 1500}, function () {
                    errorRedirect();
                });
            }
        });
    });

});