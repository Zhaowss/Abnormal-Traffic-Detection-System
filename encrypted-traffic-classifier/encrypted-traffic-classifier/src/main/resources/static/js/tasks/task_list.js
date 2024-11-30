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
        url: '/task/list',
        toolbar: '#currentToolBar',
        defaultToolbar: ['filter', 'exports', 'print'],
        cols: [
            [
                {type: "checkbox", width: 50},
                {field: 'id', title: '编号', width: 100, align: 'center', sort: true},
                {
                    field: 'type', title: '上传流量类型', minWidth: 150, sort: true, templet: function (item) {
                        if (item.type === "0") {
                            return '<span class="layui-badge layui-bg-green">加密良性流量</span>';
                        } else if (item.type === "1") {
                            return '<span class="layui-badge layui-bg-cyan">加密恶意流量</span>';
                        } else {
                            return '<span class="layui-badge layui-bg-gray">未知</span>';
                        }
                    }
                },
                {
                    field: 'status', title: '任务执行状态', minWidth: 150, sort: true, templet: function (item) {
                        if (item.status === "0") {
                            return '<span class="layui-badge layui-bg-green">未预处理</span>';
                        } else if (item.status === "1") {
                            return '<span class="layui-badge">正在预处理</span>';
                        } else if (item.status === "2") {
                            return '<span class="layui-badge layui-bg-silk-deep-blue">已预处理未分类</span>';
                        } else if (item.status === "3") {
                            return '<span class="layui-badge layui-bg-orange">正在分类</span>';
                        } else if (item.status === "4") {
                            return '<span class="layui-badge layui-bg-blue">已分类</span>';
                        } else if (item.status === "5") {
                            return '<span class="layui-badge layui-bg-silk-deep-purple">预处理失败</span>';
                        } else if (item.status === "6") {
                            return '<span class="layui-badge layui-bg-silk-deep-pink">分类失败</span>';
                        } else {
                            return '<span class="layui-badge layui-bg-gray">未知</span>';
                        }
                    }
                },
                {
                    field: 'createTime', title: '任务创建时间', minWidth: 200, sort: true, templet: function (item) {
                        if (item.createTime === null) {
                            return '<span class="layui-badge layui-bg-gray">未知</span>';
                        } else {
                            return '<span class="layui-badge layui-bg-silk-deep-green">' + item.createTime + '</span>';
                        }
                    }
                },
                {field: 'preFileName', title: '处理前文件名称', minWidth: 200},
                {field: 'preFileSize', title: '处理前文件大小（Byte）', sort: true, minWidth: 250},
                {field: 'preFilePath', title: '处理前文件路径', minWidth: 300},
                {fixed: 'right', title: '操作', minWidth: 300, align: 'center', toolbar: '#currentTableBar'}
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
        if (obj.event === 'upload') {
            miniTab.openNewTabByIframe({
                href: "taskAdd",
                title: "流量上传"
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
                        url: '/task/delete',
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
                'type': '',
                'status': '',
                'startTime': '',
                'endTime': ''
            });
            form.render();
        }
    });

    table.on('tool(currentTableFilter)', function (obj) {
        if (obj.event === 'download') {
            layer.confirm('确定下载？', {icon: 3, title: '提示', closeBtn: 2}, function (index) {
                window.location.href = '/file/download/' + obj.data.preFileName;
                layer.close(index);
            });
        } else if (obj.event === 'delete') {
            layer.confirm('确定删除？', {icon: 0, title: '警告'}, function (index) {
                $.ajax({
                    url: '/task/delete',
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
        } else if (obj.event === 'result') {
            formData = obj.data;
            var index = layer.open({
                title: '任务 <span style="color: #ff5722; font-weight: bold;">' + obj.data.id + '</span> 分类结果',
                type: 2,
                area: ['100%', '100%'],
                content: 'resultList'
            });
            $(window).on("resize", function () {
                layer.full(index);
            });
        }
    });

});