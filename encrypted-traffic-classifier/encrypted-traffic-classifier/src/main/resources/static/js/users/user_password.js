layui.use(['jquery', 'layer', 'form', 'miniTab'], function () {
    var $ = layui.jquery,
        layer = layui.layer,
        form = layui.form,
        miniTab = layui.miniTab;

    form.on('submit(confirm)', function (data) {
        var index = layer.load(0, {shade: true});
        $.ajax({
            url: '/user/password',
            type: 'POST',
            data: JSON.stringify(data.field),
            dataType: 'json',
            contentType: 'application/json;charset=UTF-8',
            success: function (res) {
                if (res.code === 200) {
                    layer.msg(res.msg, {icon: 1, time: 1500}, function () {
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