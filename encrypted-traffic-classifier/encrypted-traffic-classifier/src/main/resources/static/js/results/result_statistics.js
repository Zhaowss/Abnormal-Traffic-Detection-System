layui.use(['jquery', 'layer', 'miniTab'], function () {
    var $ = layui.jquery,
        layer = layui.layer,
        miniTab = layui.miniTab;

    miniTab.listen();

    function loadEchartsPie(elementId, url, seriesName) {
        var echartsPie = echarts.init(document.getElementById(elementId));
        var option = {
            tooltip: {},
            legend: {
                type: 'scroll',
                x: 'center',
                y: 'bottom'
            },
            series: [
                {
                    name: seriesName,
                    type: 'pie',
                    radius: '65%',
                    center: ['50%', '45%'],
                    emphasis: {
                        itemStyle: {
                            shadowBlur: 10,
                            shadowOffsetX: 0,
                            shadowColor: 'rgba(0, 0, 0, 0.5)'
                        }
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
                        series: [
                            {
                                data: res.data
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

    function loadCount(elementId, url) {
        $.ajax({
            url: url,
            type: 'GET',
            data: null,
            dataType: 'json',
            contentType: 'application/json;charset=UTF-8',
            success: function (res) {
                if (res.code === 200) {
                    $(elementId).text(res.data);
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

    window.onload = function () {
        loadCount('#userCount', '/user/count');
        loadCount('#trafficCount', '/traffic/count');
        loadCount('#taskCount', '/task/count');
        loadCount('#resultCount', '/result/count');
        loadEchartsPie('echarts-pie-benign', '/result/stat/pie/0', '加密良性流量');
        loadEchartsPie('echarts-pie-malicious', '/result/stat/pie/1', '加密恶意流量');
    };

    setInterval(function () {
        loadCount('#userCount', '/user/count');
    }, 10000);

    setInterval(function () {
        loadCount('#trafficCount', '/traffic/count');
    }, 10000);

    setInterval(function () {
        loadCount('#taskCount', '/task/count');
    }, 10000);

    setInterval(function () {
        loadCount('#resultCount', '/result/count');
    }, 10000);

    setInterval(function () {
        loadEchartsPie('echarts-pie-benign', '/result/stat/pie/0', '加密良性流量');
    }, 10000);

    setInterval(function () {
        loadEchartsPie('echarts-pie-malicious', '/result/stat/pie/1', '加密恶意流量');
    }, 10000);

});