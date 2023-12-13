
$(function() {
    function fetchDataYear() {
        var apiUrl = "/rest/list-years";

        $.ajax({
            url: apiUrl,
            type: 'GET',
            success: function(data) {
                console.log(data);
                populateDropdown(data);

                var initialSelectedText = $(".select-year option:first").text();
                if (initialSelectedText.trim() !== "") {
                    fetchData(initialSelectedText);
                }
            }
        });
    }

    function populateDropdown(data) {
        var select = $(".select-year");

        // Clear existing options
        select.empty();

        // Add options from the fetched data
        for (var i = 0; i < data.length; i++) {
            var year = data[i][0];
            var optionValue = i + 1;
            var optionText = year;

            // Create option element
            var option = $("<option></option>")
                .attr("value", optionValue)
                .text(optionText);

            // Append option to select element
            select.append(option);
        }
    }

    $(".select-year").change(function() {
        var selectedText = $(this).find("option:selected").text();

        if (selectedText.trim() !== "") {
            fetchData(selectedText);
        }
    });

    fetchDataYear();

    function fetchData(selectedText) {
        console.log(selectedText);
        var apiUrl = "/rest/statisticl?year=" + selectedText;

        $.ajax({
            url: apiUrl,
            type: 'GET',
            success: function(data) {
                console.log(data);
                var newData = processData(data);
                var newDataMonth = processDataMonth(data);
                updateChart(newData, newDataMonth);
            }
        });
    }

    function processData(apiData) {
        var newData = [];
        for (let i = 0; i < apiData.length; i++) {
            var total = apiData[i][2];
            if (!isNaN(total)) {
                newData.push(total);
            }
        }
        return newData;
    }

    function processDataMonth(apiDataMonth) {
        var newDataMonth = [];
        for (let i = 0; i < apiDataMonth.length; i++) {
            var month = apiDataMonth[i][0].toString();
            newDataMonth.push(month);
        }
        return newDataMonth;
    }

    fetchData();

    var chart = {
        series: [{
            name: "Thu nhập tháng này:",
            data: []
        }, ],

        chart: {
            type: "bar",
            height: 345,
            offsetX: -15,
            toolbar: { show: true },
            foreColor: "#025464",
            fontFamily: 'inherit',
            sparkline: { enabled: false },
        },


        colors: ["#E57C23", "#E8AA42"],


        plotOptions: {
            bar: {
                horizontal: false,
                columnWidth: "35%",
                borderRadius: [6],
                borderRadiusApplication: 'end',
                borderRadiusWhenStacked: 'all'
            },
        },

        markers: { size: 0 },

        dataLabels: {
            enabled: false,
        },


        legend: {
            show: false,
        },


        grid: {
            borderColor: "rgba(0,0,0,0.1)",
            strokeDashArray: 3,
            xaxis: {
                lines: {
                    show: false,
                },
            },
        },

        xaxis: {
            type: "category",
            categories: [],
            labels: {
                style: { cssClass: "grey--text lighten-2--text fill-color" },
            },
        },


        yaxis: {
            show: true,
            min: 0,
            max: 5000000,
            tickAmount: 10,
            labels: {
                style: {
                    cssClass: "grey--text lighten-2--text fill-color",
                },
            },
        },
        stroke: {
            show: true,
            width: 3,
            lineCap: "butt",
            colors: ["transparent"],
        },


        tooltip: { theme: "light" },

        responsive: [{
            breakpoint: 600,
            options: {
                plotOptions: {
                    bar: {
                        borderRadius: 3,
                    }
                },
            }
        }]


    };

    var myChart = new ApexCharts(document.querySelector("#chart"), chart);
    myChart.render();

    function updateChart(newData, newDataMonth) {
        console.log(newDataMonth);

        myChart.updateOptions({
            xaxis: {
                categories: newDataMonth,
                labels: {
                    style: { cssClass: "grey--text lighten-2--text fill-color" },
                },
            },
            series: [{ name: "Thu nhập tháng này:", data: newData }],
        });
    }

    function fetchDataBreakup() {
        var apiData = "/rest/income-in-recent-years";
        $.ajax({
            url: apiData,
            type: 'GET',
            success: function(dataFromApi) {
                var totalValue = dataFromApi.reduce((sum, item) => sum + item[1], 0);

                var percentages = dataFromApi.map(item => Math.round((item[1] / totalValue) * 100));

                var breakup = {
                    color: "#adb5bd",
                    series: percentages,
                    labels: dataFromApi.map(item => item[0].toString()),
                    chart: {
                        width: 180,
                        type: "donut",
                        fontFamily: "Plus Jakarta Sans', sans-serif",
                        foreColor: "#adb0bb",
                    },
                    plotOptions: {
                        pie: {
                            startAngle: 0,
                            endAngle: 360,
                            donut: {
                                size: '75%',
                            },
                        },
                    },
                    stroke: {
                        show: false,
                    },

                    dataLabels: {
                        enabled: false,
                    },

                    legend: {
                        show: false,
                    },
                    colors: ["#E8AA42", "#E57C23", "#F8F1F1"],

                    responsive: [{
                        breakpoint: 991,
                        options: {
                            chart: {
                                width: 150,
                            },
                        },
                    }, ],
                    tooltip: {
                        theme: "dark",
                        fillSeriesColor: false,
                    },
                };

                var chart = new ApexCharts(document.querySelector("#breakup"), breakup);
                chart.render();
            }
        });

    }

    fetchDataBreakup();

    var earning = {
        chart: {
            id: "sparkline3",
            type: "area",
            height: 60,
            sparkline: {
                enabled: true,
            },
            group: "sparklines",
            fontFamily: "Plus Jakarta Sans', sans-serif",
            foreColor: "#adb0bb",
        },
        series: [{
            name: "Thu nhập",
            color: "#E57C23",
            data: [25, 66, 20, 40, 12, 58, 20],
        }, ],
        stroke: {
            curve: "smooth",
            width: 2,
        },
        fill: {
            colors: ["#f3feff"],
            type: "solid",
            opacity: 0.05,
        },

        markers: {
            size: 0,
        },
        tooltip: {
            theme: "dark",
            fixed: {
                enabled: true,
                position: "right",
            },
            x: {
                show: false,
            },
        },
    };

    new ApexCharts(document.querySelector("#earning"), earning).render();
})