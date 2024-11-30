layui.use(['jquery', 'layer', 'form'], function () {
    var $ = layui.jquery,
        layer = layui.layer,
        form = layui.form;

    form.on('submit(confirm)', function (data) {
        var index = layer.load(0, {shade: true});
        $.ajax({
            url: '/user/add',
            type: 'POST',
            data: JSON.stringify(data.field),
            dataType: 'json',
            contentType: 'application/json;charset=UTF-8',
            success: function (res) {
                if (res.code === 200) {
                    layer.msg(res.msg, {icon: 1, time: 1500}, function () {
                        var iframeIndex = parent.layer.getFrameIndex(window.name);
                        parent.layer.close(iframeIndex);
                    });
                } else {
                    layer.msg(res.msg, {icon: 2, time: 1500}, function () {
                        var iframeIndex = parent.layer.getFrameIndex(window.name);
                        parent.layer.close(iframeIndex);
                    });
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