layui.use(['jquery', 'layer', 'form', 'table', 'miniTab', 'laydate'], function () {
    var $ = layui.jquery,
        layer = layui.layer,
        form = layui.form,
        table = layui.table,
        miniTab = layui.miniTab,
        laydate = layui.laydate;

    laydate.render({
        elem: '#range-time',
        range: ['#start-time', '#end-time'],
        type: 'datetime',
        theme: '#1E9FFF',
        rangeLinked: true
    });

    table.render({
        elem: '#currentTableId',
        id: 'currentTableId',
        url: '/traffic/list',
        toolbar: '#currentToolBar',
        defaultToolbar: ['filter', 'exports', 'print'],
        cols: [
            [
                {type: "checkbox", width: 50},
                {field: 'id', title: '编号', width: 100, align: 'center', sort: true},
                {
                    field: 'createTime', title: '流量捕获时间', minWidth: 200, sort: true, templet: function (item) {
                        if (item.createTime === null) {
                            return '<span class="layui-badge layui-bg-gray">未知</span>';
                        } else {
                            return '<span class="layui-badge layui-bg-silk-deep-green">' + item.createTime + '</span>';
                        }
                    }
                },
                {field: 'protocol', title: '协议过滤', minWidth: 100},
                {field: 'port', title: '端口过滤', minWidth: 100},
                {field: 'sourceIp', title: '源IP地址过滤', minWidth: 150},
                {field: 'destinationIp', title: '目的IP地址过滤', minWidth: 150},
                {field: 'timeLength', title: '时间长度（s）', sort: true, minWidth: 150},
                {field: 'fileName', title: '文件名称', minWidth: 200},
                {field: 'fileSize', title: '文件大小（Byte）', sort: true, minWidth: 200},
                {field: 'filePath', title: '文件路径', minWidth: 300},
                {fixed: 'right', title: '操作', minWidth: 200, align: 'center', toolbar: '#currentTableBar'}
            ]
        ],
        limits: [5, 10, 15, 20, 25, 50, 100],
        limit: 5,
        page: true,
        skin: 'line',
        size: 'lg',
        response: {
            statusCode: 200
        },
        done: function (res, curr, count) {
            if (res.data.length === 0 && curr > 1 && count !== 0) {
                curr = curr - 1;
                table.reload('currentTableId', {
                    page: {
                        curr: curr
                    },
                    where: {}
                });
            }
        },
        error: function (error) {
            layer.msg(error, {icon: 2, time: 1500}, function () {
                errorRedirect();
            });
        }
    });

    form.on('submit(data-search-btn)', function (data) {
        table.reload('currentTableId', {
            page: {
                curr: 1
            },
            where: data.field
        });
        return false;
    });

    table.on('toolbar(currentTableFilter)', function (obj) {
        if (obj.event === 'gather') {
            miniTab.openNewTabByIframe({
                href: "trafficGather",
                title: "流量捕获",
            });
        } else if (obj.event === 'delete-batch') {
            var dataList = table.checkStatus('currentTableId').data;
            var idList = [];
            for (var i = 0; i < dataList.length; i++) {
                idList.push(dataList[i].id);
            }
            if (idList.length > 0) {
                layer.confirm('确定删除？', {icon: 0, title: '警告'}, function (index) {
                    $.ajax({
                        url: '/traffic/delete',
                        type: "DELETE",
                        data: JSON.stringify({'idList': idList}),
                        dataType: 'json',
                        contentType: 'application/json;charset=UTF-8',
                        success: function (res) {
                            if (res.code === 200) {
                                layer.msg(res.msg, {icon: 1, time: 1500}, function () {
                                    table.reload('currentTableId');
                                });
                            } else {
                                layer.msg(res.msg, {icon: 2, time: 1500}, function () {
                                    table.reload('currentTableId');
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
                });
            } else {
                layer.msg("未选中信息", {icon: 2, time: 1500});
            }
        } else if (obj.event === 'refresh') {
            table.reload('currentTableId', {
                page: {
                    curr: 1
                },
                where: {}
            });
            form.val('data-search-form', {
                'protocol': '',
                'port': '',
                'sourceIp': '',
                'destinationIp': '',
                'startTime': '',
                'endTime': ''
            });
            form.render();
        }
    });

    table.on('tool(currentTableFilter)', function (obj) {
        if (obj.event === 'download') {
            layer.confirm('确定下载？', {icon: 3, title: '提示', closeBtn: 2}, function (index) {
                window.location.href = '/file/download/' + obj.data.fileName;
                layer.close(index);
            });
        } else if (obj.event === 'delete') {
            layer.confirm('确定删除？', {icon: 0, title: '警告'}, function (index) {
                $.ajax({
                    url: '/traffic/delete',
                    type: "DELETE",
                    data: JSON.stringify({'idList': [obj.data.id]}),
                    dataType: 'json',
                    contentType: 'application/json;charset=UTF-8',
                    success: function (res) {
                        if (res.code === 200) {
                            layer.msg(res.msg, {icon: 1, time: 1500}, function () {
                                table.reload('currentTableId');
                            });
                        } else {
                            layer.msg(res.msg, {icon: 2, time: 1500}, function () {
                                table.reload('currentTableId');
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
            });
        }
    });

});