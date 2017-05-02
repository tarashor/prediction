var config = {
    type: 'line',
    data: {
        labels: [],
        datasets: [
            {
                label:"Summary",
                backgroundColor: "rgba(76, 74, 191, 0.2)",
                borderColor: "rgba(76, 74, 191, 1)",
                data: [],
                fill: false
            },
            {
                label:"Before border",
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

function setDataForChart(l, beforeBorder, summary) {
   config.data.labels = l;
   config.data.datasets[0].data = summary;
   config.data.datasets[1].data = beforeBorder;
   statisticChart.update();
}


function setLabelsForChart(title, xAxis, yAxis, summaryLabel, beforeBorderLabel) {
    config.options.title.text = title;
    config.options.scales.xAxes[0].scaleLabel.labelString = xAxis;
    config.options.scales.yAxes[0].scaleLabel.labelString = yAxis;
    config.data.datasets[0].label = summaryLabel;
    config.data.datasets[1].label = beforeBorderLabel;
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
            var bb = [];
            var s = [];
            for (var i = 0; i < res.length; i++) {
                var dateStr = moment(res[i].date).format("D.MM.YYYY HH:mm");
                l.push(dateStr);
                bb.push(res[i].carsCountBeforeBorder);
                s.push(res[i].carsCountOnBorder);
            }
            setDataForChart(l, bb, s);
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
}

$(document).ready(function () {
    var chartJQ = $("#statisticChart");

    var chartTitle = chartJQ.data("title");
    var xAxis = chartJQ.data("x_axis");
    var yAxis = chartJQ.data("y_axis");
    var chartSummary = chartJQ.data("dataset_summary_label");
    var chartBeforeBorder = chartJQ.data("dataset_before_border_label");
    setLabelsForChart(chartTitle, xAxis, yAxis, chartSummary, chartBeforeBorder);

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

