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
                    suggestedMax: 200
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

function fillinPasses(options) {
    $("#passes").html("");
    var passesHtml = "";
    for (var i = 0; i < options.length; i++) {
        passesHtml += "<option>" + options[i] + "</option>";
    }
    $("#passes").html(passesHtml);
}

function loadPassStatistic(passName) {
    $.get(statUrl, { pass: passName }, function (res) {
        res.reverse();
            var l = [];
            var bb = [];
            var s = [];
            for (var i = 0; i < res.length; i++) {
                l.push((new Date(res[i].date)).toISOString());
                bb.push(res[i].carsCountBeforeBorder);
                s.push(res[i].carsCountBeforeBorder + res[i].carsCountOnBorder);
            }
            setDataForChart(l, bb, s);
        });
}

function loadPassesList() {
    $.get(passesUrl, function (res) {
        fillinPasses(res);
        loadPassStatistic($("#passes").val())
    });
}

$(document).ready(function () {
    $("#passes").change(function () {
        loadPassStatistic($(this).val())
    });
    var ctx2 = $("#statisticChart").get(0).getContext("2d");
    statisticChart = new Chart(ctx2, config);
    loadPassesList();
});

