layui.use(['jquery', 'layer', 'form', 'miniTab'], function () {
    var $ = layui.jquery,
        layer = layui.layer,
        form = layui.form,
        miniTab = layui.miniTab;

    $.ajax({
        url: '/user/info',
        type: 'GET',
        data: null,
        dataType: 'json',
        contentType: 'application/json;charset=UTF-8',
        success: function (res) {
            if (res.code === 200) {
                form.val("currentFormFilter", {
                    'userName': res.data.userName
                });
                form.render();
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

    form.on('submit(confirm)', function (data) {
        var index = layer.load(0, {shade: true});
        $.ajax({
            url: '/user/setting',
            type: 'POST',
            data: JSON.stringify(data.field),
            dataType: 'json',
            contentType: 'application/json;charset=UTF-8',
            success: function (res) {
                if (res.code === 200) {
                    layer.msg(res.msg, {icon: 1, time: 1500}, function () {
                        $(window.parent.document).find("#userName").html(data.field.userName);
                        miniTab.deleteCurrentByIframe();
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
        return false;
    });

});