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
    }
}

$(
   function (){
   console.log("fff");
   $("#zoomSlider").slider({min: 8, max: 24, change: 
           function(){
               console.log($(this).slider("value"));
           } });
   
});

