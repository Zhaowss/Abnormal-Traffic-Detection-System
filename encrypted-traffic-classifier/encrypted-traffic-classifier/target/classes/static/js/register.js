layui.use(['jquery', 'layer', 'form'], function () {
    var $ = layui.jquery,
        layer = layui.layer,
        form = layui.form;

    if (top.location !== self.location) {
        top.location = self.location;
    }

    $(document).ready(function () {
        $('.layui-container').particleground({
            dotColor: '#7ec7fd',
            lineColor: '#7ec7fd'
        });
    });

    form.on('submit(register)', function (data) {
        var index = layer.load(0, {shade: true});
        $.ajax({
            url: '/user/register',
            type: 'POST',
            data: JSON.stringify(data.field),
            dataType: 'json',
            contentType: 'application/json;charset=UTF-8',
            success: function (res) {
                if (res.code === 200) {
                    layer.msg(res.msg, {icon: 1, time: 1500}, function () {
                        loginRedirect();
                    });
                } else {
                    layer.msg(res.msg, {icon: 2, time: 1500}, function () {
                        $("#captcha").click();
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