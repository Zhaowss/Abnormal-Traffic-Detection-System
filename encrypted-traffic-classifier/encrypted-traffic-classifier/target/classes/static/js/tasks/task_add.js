layui.use(['jquery', 'layer', 'form', 'upload', 'element', 'miniTab'], function () {
    var $ = layui.jquery,
        layer = layui.layer,
        form = layui.form,
        upload = layui.upload,
        element = layui.element,
        miniTab = layui.miniTab;

    var preFileName = null;
    var preFileSize = null;
    var preFilePath = null;

    upload.render({
        elem: '#upload-file',
        url: 'file/upload',
        accept: 'file',
        exts: 'pcap',
        before: function () {
            element.progress('upload-progress', '0%');
            layer.msg('上传中...', {icon: 16, time: 0});
        },
        done: function (res) {
            if (res.code === 200) {
                layer.msg(res.msg, {icon: 1, time: 1500}, function () {
                    preFileName = res.data.fileName;
                    preFileSize = res.data.fileSize;
                    preFilePath = res.data.filePath;
                });
            } else {
                layer.msg(res.msg, {icon: 2, time: 1500});
            }
        },
        progress: function (n) {
            element.progress('upload-progress', n + '%');
        },
        error: function (error) {
            layer.msg(error, {icon: 2, time: 1500}, function () {
                errorRedirect();
            });
        }
    });

    form.on('submit(confirm)', function (data) {
        if (preFileName == null || preFileSize == null || preFilePath == null) {
            layer.msg('文件没有上传完成', {icon: 2, time: 1500});
        } else {
            data.field.preFileName = preFileName;
            data.field.preFileSize = preFileSize;
            data.field.preFilePath = preFilePath;
            var index = layer.load(0, {shade: true});
            $.ajax({
                url: '/task/add',
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
        }
    });

});