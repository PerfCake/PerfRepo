
var previousAdded;
var previousHighlightedPointX;
var previousHighlightedPointSeries;
var previousHighlightedPointChart;

var highlightedPointsX;
var highlightedPointsSeries;
var highlightedPointsChart;
var highlightedPointsCounter;

metricHistoryClear();

function metricHistoryRefresh() {
    for(var i = 0; i < highlightedPointsCounter; i++)  {
        var chart = $(document.getElementById(highlightedPointsChart[i])).chart('getPlotObject');

        if(typeof(chart.highlight) === "function") {
            chart.highlight(highlightedPointsSeries[i], highlightedPointsX[i]);
        }
    }

    var chart = $(document.getElementById(previousHighlightedPointChart)).chart('getPlotObject');
    if(typeof(chart.highlight) === "function") {
        chart.highlight(previousHighlightedPointSeries, previousHighlightedPointX);
    }
}

function metricHistoryPointClick(e) {
    var currentChartName = e.currentTarget.name + 'Chart';
    var chart = $(document.getElementById(currentChartName)).chart('getPlotObject');

    if(!previousAdded && previousHighlightedPointChart != null) {
        var previousChart = $(document.getElementById(previousHighlightedPointChart)).chart('getPlotObject');
        if(typeof(previousChart.unhighlight) === "function") {
            previousChart.unhighlight(previousHighlightedPointSeries, previousHighlightedPointX);
        }
    }

    previousAdded = false;
    previousHighlightedPointX = e.data.x;
    previousHighlightedPointSeries = e.data.seriesIndex;
    previousHighlightedPointChart = currentChartName;

    //no need to check because if I clicked on something, it definitely exists :)
    chart.highlight(e.data.seriesIndex, e.data.x);
}

function metricHistoryPermanentlyHighlight() {
    previousAdded = true;

    highlightedPointsX[highlightedPointsCounter] = previousHighlightedPointX;
    highlightedPointsSeries[highlightedPointsCounter] = previousHighlightedPointSeries;
    highlightedPointsChart[highlightedPointsCounter] = previousHighlightedPointChart;

    highlightedPointsCounter++;
}

function metricHistoryClear() {
    previousAdded = false;
    previousHighlightedPointX = -1;
    previousHighlightedPointSeries = -1;
    previousHighlightedPointChart = null;

    highlightedPointsX = new Array();
    highlightedPointsSeries = new Array();
    highlightedPointsChart = new Array();
    highlightedPointsCounter = 0;
}
