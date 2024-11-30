layui.use(['jquery', 'layer', 'table'], function () {
    var $ = layui.jquery,
        layer = layui.layer,
        table = layui.table;

    var formData = parent.formData;

    table.render({
        elem: '#currentTableId',
        id: 'currentTableId',
        url: '/result/list',
        where: {
            'taskId': formData.id
        },
        cols: [
            [
                {field: 'id', title: '编号', width: 100, align: 'center', sort: true},
                {
                    field: 'confidence', title: '置信度', minWidth: 350, templet: function (item) {
                        if (item.confidence === null) {
                            return '<span class="layui-badge layui-bg-gray">未知</span>';
                        } else {
                            var confidenceStr = item.confidence.replace(/'/g, '"');
                            var confidenceJson = JSON.parse(confidenceStr);
                            var confidenceHtml = `<ul>`;
                            for (var key in confidenceJson) {
                                if (confidenceJson.hasOwnProperty(key)) {
                                    if (key === item.category) {
                                        confidenceHtml += `
                                        <li">
                                            <i class="layui-icon layui-icon-circle-dot"></i>&nbsp;&nbsp;<strong>${key}:</strong> ${confidenceJson[key]}
                                        </li>
                                        <br>
                                    `;
                                    } else {
                                        confidenceHtml += `
                                        <li">
                                            <i class="layui-icon layui-icon-radio"></i>&nbsp;&nbsp;<strong>${key}:</strong> ${confidenceJson[key]}
                                        </li>
                                        <br>
                                    `;
                                    }
                                }
                            }
                            confidenceHtml += `</ul>`;
                            return confidenceHtml;
                        }
                    }
                },
                {
                    field: 'category', title: '类别', minWidth: 100, sort: true, templet: function (item) {
                        if (item.category === null) {
                            return '<span class="layui-badge layui-bg-gray">未知</span>';
                        } else {
                            return '<span class="layui-badge layui-bg-blue">' + item.category + '</span>';
                        }
                    }
                },
                {field: 'postFileName', title: '处理后文件名称', minWidth: 200},
                {field: 'postFileSize', title: '处理后文件大小（Byte）', sort: true, minWidth: 250},
                {field: 'postFilePath', title: '处理后文件路径', minWidth: 300},
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
                    where: {
                        'taskId': formData.id
                    }
                });
            }
        },
        error: function (error) {
            layer.msg(error, {icon: 2, time: 1500}, function () {
                errorRedirect();
            });
        }
    });

    table.on('tool(currentTableFilter)', function (obj) {
        if (obj.event === 'download') {
            layer.confirm('确定下载？', {icon: 3, title: '提示', closeBtn: 2}, function (index) {
                var parts = obj.data.postFilePath.split("\\");
                var parentName = parts[parts.length - 2];
                window.location.href = '/file/download/' + obj.data.postFileName + '?parentName=' + parentName;
                layer.close(index);
            });
        } else if (obj.event === 'delete') {
            layer.confirm('确定删除？', {icon: 0, title: '警告'}, function (index) {
                $.ajax({
                    url: '/result/delete',
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
        } else if (obj.event === 'statistics') {
            childFormData = obj.data;
            layer.open({
                title: '结果 <span style="color: #ff5722; font-weight: bold;">' + obj.data.id + '</span> 分类统计',
                type: 2,
                shadeClose: true,
                area: ['80%', '80%'],
                content: 'confidenceStatistics'
            });
        }
    });

    function loadEchartsBar(elementId, url, seriesName) {
        var echartsPie = echarts.init(document.getElementById(elementId));
        var option = {
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'shadow'
                }
            },
            grid: {
                left: '3%',
                right: '4%',
                top: '6%',
                bottom: '3%',
                containLabel: true
            },
            xAxis: {
                type: 'category',
                axisTick: {
                    alignWithLabel: true
                },
                axisLabel: {
                    interval: 0,
                    rotate: -35
                },
                data: []
            },
            yAxis: {
                type: 'value',
                min: 0,
                max: 1
            },
            series: [
                {
                    name: seriesName,
                    type: 'bar',
                    barWidth: '40%',
                    showBackground: true,
                    backgroundStyle: {
                        color: 'rgba(180, 180, 180, 0.2)'
                    },
                    data: []
                }
            ]
        };
        echartsPie.setOption(option);
        window.onresize = function () {
            echartsPie.resize();
        };

        echartsPie.showLoading({
            text: '加载中...',
            fontSize: 14,
            color: '#1E9FFF',
            textColor: '#1E9FFF'
        });

        $.ajax({
            url: url,
            type: 'GET',
            data: null,
            dataType: 'json',
            contentType: 'application/json;charset=UTF-8',
            success: function (res) {
                if (res.code === 200) {
                    echartsPie.setOption({
                        xAxis: {
                            data: Object.keys(res.data)
                        },
                        series: [
                            {
                                data: Object.values(res.data)
                            }
                        ]
                    });
                    echartsPie.hideLoading();
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
    }

    loadEchartsBar('echarts-bar', '/result/stat/barAvg/' + formData.id, '平均置信度');

    $.ajax({
        url: '/result/finalResult/' + formData.id,
        type: 'GET',
        data: null,
        dataType: 'json',
        contentType: 'application/json;charset=UTF-8',
        success: function (res) {
            if (res.code === 200) {
                $("#finalResult").html(res.data);
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

});