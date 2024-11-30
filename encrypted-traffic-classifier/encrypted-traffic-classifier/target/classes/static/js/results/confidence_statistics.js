layui.use(['jquery', 'layer'], function () {
    var $ = layui.jquery,
        layer = layui.layer;

    var formData = parent.childFormData;

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

    loadEchartsBar('echarts-bar', '/result/stat/bar/' + formData.id, '置信度');

});