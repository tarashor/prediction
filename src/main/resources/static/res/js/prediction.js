var config = {
    type: 'line',
    data: {
        labels: [],
        datasets: [
            {
                label:"Statistic",
                backgroundColor: "rgba(76, 74, 191, 0.2)",
                borderColor: "rgba(76, 74, 191, 1)",
                data: [],
                fill: false
            },
            {
                label:"Prediction",
                backgroundColor: "rgba(75, 192, 192, 0.2)",
                borderColor: "rgba(75, 192, 192, 1)",
                data: [],
                fill: true
            }
        ]
    },
    options: {
        responsive: true,
        maintainAspectRatio: false,
        title: {
            display: true,
            text: 'Statistic on border'
        },
        tooltips: {
            mode: 'index',
            intersect: false
        },
        hover: {
            mode: 'nearest',
            intersect: true
        },
        scales: {
            xAxes: [{
                display: true,
                scaleLabel: {
                    display: true,
                    labelString: 'Date'
                }
            }],
            yAxes: [{
                display: true,
                scaleLabel: {
                    display: true,
                    labelString: 'Cars count'
                },
                ticks: {
                    beginAtZero: true, // minimum value will be 0.
                    suggestedMin: 0,
                    suggestedMax: 600
                }
            }]
        }
    }
};

function setDataForChart(l, statistic, prediction) {
   config.data.labels = l;
   config.data.datasets[0].data = statistic;
   config.data.datasets[1].data = prediction;
   statisticChart.update();
}


function setLabelsForChart(title, xAxis, yAxis, statisticLabel, predictionLabel) {
    config.options.title.text = title;
    config.options.scales.xAxes[0].scaleLabel.labelString = xAxis;
    config.options.scales.yAxes[0].scaleLabel.labelString = yAxis;
    config.data.datasets[0].label = statisticLabel;
    config.data.datasets[1].label = predictionLabel;
}

function fillinPasses(options) {
    $("#passes").html("");
    var passesHtml = "";
    for (var i = 0; i < options.length; i++) {
        passesHtml += "<option>" + options[i] + "</option>";
    }
    $("#passes").html(passesHtml);
}

function loadPassStatistic(passName, startDateMilliseconds, endDateMilliseconds) {
    $.get(statUrl,
        {
            pass: passName,
            start: startDateMilliseconds,
            end: endDateMilliseconds
        },
        function (res) {
            res.reverse();
            var l = [];
            var stat = [];
            var pred = [];
            for (var i = 0; i < res.length; i++) {
                var dateStr = moment(res[i].date).format("D.MM.YYYY HH:mm");
                l.push(dateStr);
                stat.push(res[i].statistic);
                pred.push(res[i].prediction);
            }
            setDataForChart(l, stat, pred);
        });
}

function loadPassesList() {
    $.get(passesUrl, function (res) {
        fillinPasses(res);
        loadPassStatisticWithValues();
    });
}

function loadPassStatisticWithValues(){
    loadPassStatistic($("#passes").val(), $('#datetimepickerStart').data("DateTimePicker").date().valueOf(), $('#datetimepickerEnd').data("DateTimePicker").date().valueOf())
    loadErrors($("#passes").val());
}

function loadErrors(passName){
    $.get("/api/prederror",
            {
                pass: passName
            },
            function (res) {
                $("#predictionError").html(res);
            });

    $.get("/api/prederrorbest",
            {
                pass: passName
            },
            function (res) {
                $("#bestError").html(res);

            });
}


$(document).ready(function () {
    var chartJQ = $("#statisticChart");

    var chartTitle = chartJQ.data("title");
    var xAxis = chartJQ.data("x_axis");
    var yAxis = chartJQ.data("y_axis");
    var chartStat = chartJQ.data("dataset_statistic");
    var chartPred = chartJQ.data("dataset_prediction");
    setLabelsForChart(chartTitle, xAxis, yAxis, chartStat, chartPred);

    $('#datetimepickerStart').datetimepicker({
        format: "DD.MM.YYYY"
    });
    $('#datetimepickerEnd').datetimepicker({
        format: "DD.MM.YYYY",
        useCurrent: false //Important! See issue #1075
    });

    var now = moment();
    var twoWeeksAgo = moment().subtract(14, 'days');

    $('#datetimepickerStart').data("DateTimePicker").date(twoWeeksAgo);
    $('#datetimepickerEnd').data("DateTimePicker").date(now);

    $("#datetimepickerStart").on("dp.change", function (e) {
        $('#datetimepickerEnd').data("DateTimePicker").minDate(e.date);
        loadPassStatisticWithValues();

    });
    $("#datetimepickerEnd").on("dp.change", function (e) {
        $('#datetimepickerStart').data("DateTimePicker").maxDate(e.date);
        loadPassStatisticWithValues();
    });
    $("#passes").change(function () {
        loadPassStatisticWithValues();
    });
    var ctx2 = chartJQ.get(0).getContext("2d");
    statisticChart = new Chart(ctx2, config);
    loadPassesList();
});

