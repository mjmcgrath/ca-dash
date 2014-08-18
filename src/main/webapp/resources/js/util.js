/* 
 * RO-CART utility scripts
 */

function dailyChartExtender(){
    var interval = 1;
        var items = this.cfg.axes.xaxis.ticks.length;
        if (items > 21) { interval = 7; }
        else if (items > 45) { interval = 30; }
        else if (items > 500) { interval = 365; }
        for (var i = 0; i < this.cfg.axes.xaxis.ticks.length; i++) {
        if (i % interval != 0) { 
            this.cfg.axes.xaxis.ticks[i] = "";
        }
        
        this.cfg.axes.yaxis.numberTicks = 20;
    }
}

function resizeCharts(){
    this._height = (window.innerHeight * .8);
    jqResizeCharts();
}

function jqResizeCharts() {
    if($("#charts\\:dailyBarChart")) {
        $("#charts\\:dailyBarChart").height(window.innerHeight * .8);
    }
    if($("#charts\\:weeklyBarChart")) {
        $("#charts\\:weeklyBarChart").height(window.innerHeight * .8);
    }
    if($("#charts\\:monthlyBarChart")) {
         $("#charts\\:monthlyBarChart").height(window.innerHeight * .8);
    }
    if($("#histo")) {
         $("#histo").height(window.innerHeight * .8);
    } 
}

function onWindowResize(){
    jqResizeCharts();
    var offset = 0;
    if($('.ui-layout-west').css('display') === 'block'){
        offset = $('.ui-layout-west').width();
    }
    if($("#charts\\:dailyBarChart")) {
        $("#charts\\:dailyBarChart").width(window.innerWidth * .93 - offset);
    }
    if($("#charts\\:weeklyBarChart")) {
        $("#charts\\:weeklyBarChart").width(window.innerWidth * .93 - offset);
    }
    if($("#charts\\:monthlyBarChart")) {
         $("#charts\\:monthlyBarChart").width(window.innerWidth * .93 - offset);
    } 
    if($("#histo")) {
         $("#histo").width(window.innerWidth * .93 - offset);
    } 
    if(PrimeFaces.widgets.barChartD) {
        PrimeFaces.widgets.barChartD.plot.replot();
    }
    if(PrimeFaces.widgets.barChartW) {
        PrimeFaces.widgets.barChartW.plot.replot();
    }
    if(PrimeFaces.widgets.barChartM) {
        PrimeFaces.widgets.barChartM.plot.replot();
    }
    
}

$(
   function (){
   $("#zoomSlider").slider({value: 1, min: 0.5, max: 2, step: 0.1, change: 
           function(){
               $("#charts\\:dailyBarChart").css("zoom", $("#zoomSlider").slider("value") );
               $("#charts\\:weeklyBarChart").css("zoom", $("#zoomSlider").slider("value") );
               $("#charts\\:monthlyBarChart").css("zoom", $("#zoomSlider").slider("value") );
           } });
       
    /*$("#charts\\:dailyBarChart").height($(window).height() - 323);
    $("#charts\\:weeklyBarChart").height($(window).height() - 323);
    $("#charts\\:monthlyBarChart").height($(window).height() - 323);
    $("#histo").height($(window).height() - 323);
    $("#physicianWorkload").height($(window).height() - 223);
    
    $("#j_idt15\\:filters\\:submit").click(function(){
        $("#charts\\:dailyBarChart").height($(window).height() - 323);
        $("#charts\\:weeklyBarChart").height($(window).height() - 323);
        $("#charts\\:monthlyBarChart").height($(window).height() - 323);
        $("#charts [role='tab'] a").click(function(){
            console.log("asdasd");
            $("#charts\\:dailyBarChart").height($(window).height() - 323);
            $("#charts\\:weeklyBarChart").height($(window).height() - 323);
            $("#charts\\:monthlyBarChart").height($(window).height() - 323);
        });
    });*/
   $.jqplot.postInitHooks.push(resizeCharts);
   jqResizeCharts();
   $(document).bind('DOMNodeInserted',function(){
        jqResizeCharts();
    });
    
});

