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

$(
   function (){
   $("#zoomSlider").slider({value: 1, min: 0.5, max: 2, step: 0.1, change: 
           function(){
               $("#charts\\:dailyBarChart").css("zoom", $("#zoomSlider").slider("value") );
               $("#charts\\:weeklyBarChart").css("zoom", $("#zoomSlider").slider("value") );
               $("#charts\\:monthlyBarChart").css("zoom", $("#zoomSlider").slider("value") );
           } });
});

