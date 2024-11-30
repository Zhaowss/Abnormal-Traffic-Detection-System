layui.use(['jquery', 'layer', 'form', 'table'], function () {
    var $ = layui.jquery,
        layer = layui.layer,
        form = layui.form,
        table = layui.table;

    table.render({
        elem: '#currentTableId',
        id: 'currentTableId',
        url: '/user/list',
        toolbar: '#currentToolBar',
        defaultToolbar: ['filter', 'exports', 'print'],
        cols: [
            [
                {type: "checkbox", width: 50},
                {field: 'id', title: '编号', width: 100, align: 'center', sort: true},
                {field: 'userName', title: '用户名', minWidth: 100},
                {
                    field: 'password', title: '密码', minWidth: 200, templet: function (item) {
                        var passwordHtml = `
                             <div class="layui-input-wrap">
                                <input type="password" class="layui-input layui-disabled" disabled readonly value="${item.password}" lay-affix="eye">
                             </div>
                        `;
                        return passwordHtml;
                    }
                },
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
        if (obj.event === 'add') {
            layer.open({
                title: '添加用户',
                type: 2,
                maxmin: true,
                shadeClose: true,
                area: ['80%', '80%'],
                content: '/userAdd',
                end: function () {
                    table.reload('currentTableId');
                }
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
                        url: '/user/delete',
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
                'userName': ''
            });
            form.render();
        }
    });

    table.on('tool(currentTableFilter)', function (obj) {
        if (obj.event === 'edit') {
            formData = obj.data;
            layer.open({
                title: '编辑用户',
                type: 2,
                maxmin: true,
                shadeClose: true,
                area: ['80%', '80%'],
                content: 'userEdit',
                end: function () {
                    table.reload('currentTableId');
                }
            });
        } else if (obj.event === 'delete') {
            layer.confirm('确定删除？', {icon: 0, title: '警告'}, function (index) {
                $.ajax({
                    url: '/user/delete',
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