/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var dataset = {
  apples: [{"label":"one", "value":20}, 
            {"label":"two", "value":50}, 
            {"label":"three", "value":30}],
};

var width = 300,
    height = 300,
    radius = Math.min(width, height) / 2;

var color = d3.scale.category20();

var pie = d3.layout.pie()
    .sort(null).value(function(d) { return d.value; });

var piedata = pie(dataset.apples);

console.log(piedata);

var arc = d3.svg.arc()
    .innerRadius(radius - 150)
    .outerRadius(radius - 50);

var svg = d3.select("body").append("svg")
    .attr("width", width)
    .attr("height", height)
    .append("g")
    .attr("transform", "translate(" + width / 2 + "," + height / 2 + ")");

var path = svg.selectAll("path")
    .data(piedata)
    .enter().append("path")
    .attr("fill", function(d, i) { return color(i); })
    .attr("d", arc);

svg.selectAll("text").data(piedata)
    .enter()
    .append("text")
    .attr("text-anchor", "middle")
    .attr("x", function(d) {
        var a = d.startAngle + (d.endAngle - d.startAngle)/2 - Math.PI/2;
        d.cx = Math.cos(a) * (radius - 75);
        return d.x = Math.cos(a) * (radius - 20);
    })
    .attr("y", function(d) {
        var a = d.startAngle + (d.endAngle - d.startAngle)/2 - Math.PI/2;
        d.cy = Math.sin(a) * (radius - 75);
        return d.y = Math.sin(a) * (radius - 20);
    })
    .text(function(d) { return d.data.label + " [" + d.value + "] "; })
    .each(function(d) {
        var bbox = this.getBBox();
        d.sx = d.x - bbox.width/2 - 2;
        d.ox = d.x + bbox.width/2 + 2;
        d.sy = d.oy = d.y + 5;
    });

svg.append("defs").append("marker")
    .attr("id", "circ")
    .attr("markerWidth", 6)
    .attr("markerHeight", 6)
    .attr("refX", 3)
    .attr("refY", 3)
    .append("circle")
    .attr("cx", 3)
    .attr("cy", 3)
    .attr("r", 3);

svg.selectAll("path.pointer").data(piedata).enter()
    .append("path")
    .attr("class", "pointer")
    .style("fill", "none")
    .style("stroke", "black")
    .attr("marker-end", "url(#circ)")
    .attr("d", function(d) {
        if(d.cx > d.ox) {
            return "M" + d.sx + "," + d.sy + "L" + d.ox + "," + d.oy + " " + d.cx + "," + d.cy;
        } else {
            return "M" + d.ox + "," + d.oy + "L" + d.sx + "," + d.sy + " " + d.cx + "," + d.cy;
        }
    });
    
   
