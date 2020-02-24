var BubbleChart, root,
    __bind = function(fn, me) {
        return function() {
            return fn.apply(me, arguments);
        };
    };

BubbleChart = (function() {
    function BubbleChart(data) {
        this.do_filter = __bind(this.do_filter, this);
        this.use_filters = __bind(this.use_filters, this);
        this.hide_details = __bind(this.hide_details, this);
        this.show_details = __bind(this.show_details, this);
        this.hide_labels = __bind(this.hide_labels, this);
        this.display_labels = __bind(this.display_labels, this);
        this.move_towards_group = __bind(this.move_towards_group, this);
        this.display_by_group = __bind(this.display_by_group, this);
        this.move_towards_group_center = __bind(this.move_towards_group_center, this);
        this.group_by = __bind(this.group_by, this);
        this.get_distinct_values = __bind(this.get_distinct_values, this);
        this.color_by = __bind(this.color_by, this);
        this.remove_colors = __bind(this.remove_colors, this);
        this.sort = __bind(this.sort, this);
        this.get_color_map = __bind(this.get_color_map, this);
        this.get_color_map_lookup_set = __bind(this.get_color_map_lookup_set, this);
        this.get_color_map_achievement = __bind(this.get_color_map_achievement, this);
        this.move_towards_center = __bind(this.move_towards_center, this);
        this.display_group_all = __bind(this.display_group_all, this);
        this.start = __bind(this.start, this);
        this.create_vis = __bind(this.create_vis, this);
        this.create_nodes = __bind(this.create_nodes, this);
        this.data = data;
        this.width = 1140;
        this.height = 600;
        this.tooltip = CustomTooltip("my_tooltip", 240);
        this.center = {
            x: this.width / 2,
            y: this.height / 2
        };
        this.layout_gravity = -0.01;
        // DISPERSION ENTRE NODOS
        this.damper = 1.2;
        this.vis = null;
        this.force = null;
        this.circles = null;
        this.nodes = [];
        this.currentCircles = [];
         var num_max_indicadores;
        num_max_indicadores = d3.max(this.data, function(d) {
            return parseInt(d.Monto);            
        });       
        // TAMAÑO DE LOS NODOS
        this.radius_scale = d3.scale.pow().exponent(0.3).domain([1, num_max_indicadores]).range([1, 120]);
        this.create_nodes();
        this.create_vis();
        this.circles.style("fill", '#C0392B');
    }     
    var nodeColors;
    nodeColors=d3.scale.category20();
    BubbleChart.prototype.buscar=function(){
       color_by($('#color-everythings-by option:selected').val());
    };

    BubbleChart.prototype.create_nodes = function() {
        var _this = this;
        this.data.forEach(function(d, i) {
            var node;
            var radius = _this.radius_scale(_this.getRadius(d));
            node = {
                id: i,
                original: d,
                radius: radius,
                value: radius,
                x: Math.random() * _this.width,
                y: Math.random() * _this.height
            };
            _this.nodes.push(node);
        });
    };
    // CREACIÓN DE LOS NODOS, AQUÍ ESTÁN LOS EVENTOS PARA MOSTRAR LA INFORMACIÓN EN CADA NODO
    BubbleChart.prototype.create_vis = function() {
        var that,
            _this = this;
        /*this.vis = d3.select("#vis").append("svg").attr("width", this.width).attr("height", this.height).attr("id", "svg_vis");*/
        this.vis = d3.select("#vis").append("svg").attr("id", "svg_vis").attr("preserveAspectRatio", "xMinYMin meet").attr("viewBox", "0 0 1400 700");
        this.circles = this.vis.selectAll("circle").data(this.nodes, function(d) {
            return d.id;
        });
        that = this;
        this.circles.enter().append("circle").attr("r", 50).style("fill", function(d) {
            return '#C0392B';
        }).attr("stroke", "none")
            .attr("id", function(d) {
            return "bubble_" + d.id;
        }).on("mouseover", function(d, i) {
            return that.show_details(d, i, this);
        }).on("mouseout", function(d, i) {
            return that.hide_details(d, i, this);
        }).on('click', function (d, i) {
            //redirige al detalle del contrato
            window.location.href = '/opencityext/servicio/contratacion-publica/'+d.original['Contrato'];
        });

        this.circles.transition().duration(2000).style("fill-opacity", 0.55).attr("opacity", 2).attr("r", function(d) {
            return d.radius;
        });
    };
    BubbleChart.prototype.charge = function(d) {
        if (d.radius === 0) {
            return 0;
        }
        return -Math.pow(d.radius, 2);
    };
    BubbleChart.prototype.start = function() {
        this.force = d3.layout.force().nodes(this.nodes).size([this.width, this.height]);
        return this.circles.call(this.force.drag); // Efecto de arrastrar
    };
    BubbleChart.prototype.display_group_all = function() {
        var _this = this;
        this.hide_labels();
        this.force.gravity(this.layout_gravity).charge(this.charge).friction(0.8).on("tick", function(e) {
            _this.circles.each(_this.move_towards_center(e.alpha)).attr("cx", function(d) {
                return d.x;
            }).attr("cy", function(d) {
                return d.y;
            });
        });
        this.force.start();
    };
    BubbleChart.prototype.move_towards_center = function(alpha) {
        var _this = this;
        return function(d) {
            d.x = d.x + (_this.center.x - d.x) * (_this.damper + 0.02) * alpha;
            return d.y = d.y + (_this.center.y - d.y) * (_this.damper + 0.02) * alpha;
        };
    };
    // FUNCIÓN PARA MAPAEAR LOS COLORES, SE PUEDEN CAMBIAR...
    BubbleChart.prototype.get_color_map_lookup_set = function(allValuesArray) {
        var baseArray, color_map, index, value, _i, _len;
        baseArray = [
            '#C0392B', //
            'gray',    // 
            '#ffcc00', //
            '#663399', // 
            '#ff6600', //
            '#ff6666',
            "#0000D9",
            "#FF00FF",
            "#FF0033",
            "#FFCC66",
            "#66CC33",
            "#33FFCC",
            "#00A0AA",
            "#FFCCFF", 
            "#FF9933", 
            "#99FF99", 
            "#00BB00", 
            "#CCFFCC",
            "#333333", 
            "#CCCCCC", 
            "#99CCCC",
            "#FF0000"
        ];
        index = 0;
        color_map = {};
        for (_i = 0, _len = allValuesArray.length; _i < _len; _i++) {
            value = allValuesArray[_i];
            color_map[value] = baseArray[index];
            index = index + 1;
            if (index >= baseArray.length) {
                index = 0;
            }
        }
        return color_map;
    };
    BubbleChart.prototype.get_color_map = function(allValuesArray) {
        return this.get_color_map_lookup_set(allValuesArray);
    };
    BubbleChart.prototype.sort = function(allValuesArray) {
        allValuesArray.sort();
    };
    BubbleChart.prototype.remove_colors = function() {
        this.circles.transition().duration(600).style("fill", "#C0392B");
        hide_color_chart();
    };
    BubbleChart.prototype.color_by = function(what_to_color_by) {
        var allValuesArray, color_mapper,
            _this = this;
        this.what_to_color_by = what_to_color_by;
        allValuesArray = this.get_distinct_values(what_to_color_by);
        color_mapper = this.get_color_map(allValuesArray);
        // Agrega DIV para mostrar los colores
        show_color_chart(what_to_color_by, color_mapper);
        var test = this.circles.transition().duration(600).style("fill", function(d) {
            return color_mapper[d.original[what_to_color_by]];
        });
    };


    BubbleChart.prototype.get_distinct_values = function(what) {
        var allValues, allValuesArray, key, value,
            _this = this;
        allValues = {};
        //console.log(what);
        this.nodes.forEach(function(d) {
            var value;

            value = d.original[what];
            var flag = false; 
           // console.log("jjjjj--->"+value);
            if (!flag) {
                allValues[value] = true;
            }
        });
        allValuesArray = [];
        for (key in allValues) {
            value = allValues[key];
            allValuesArray.push(key);
        }
        this.sort(allValuesArray);
        return allValuesArray;
    };


    BubbleChart.prototype.group_by = function(what_to_group_by) {
        var allValuesArray, numCenters, position, total_slots,
            _this = this;
        this.what_to_group_by = what_to_group_by;
        allValuesArray = this.get_distinct_values(what_to_group_by);
        numCenters = allValuesArray.length;
        this.group_centers = {};
        this.group_labels = {};
        position = 1; // Posicion dentro del DIV
        total_slots = allValuesArray.length ;
        allValuesArray.forEach(function(i) {
            var x_position;
            x_position = _this.width * position / total_slots;
            _this.group_centers[i] = {
                x: x_position,
                y: _this.height / 1.9 // Separación entre las etiquetas y los nodos
            };
            _this.group_labels[i] = x_position;
            position = position + 1;
        });
        //console.log(this.group_labels);
        this.hide_labels();
        this.force.gravity(this.layout_gravity).charge(this.charge).friction(0.9).on("tick", function(e) {
            _this.circles.each(_this.move_towards_group_center(e.alpha)).attr("cx", function(d) {
                return d.x;
            }).attr("cy", function(d) {
                return d.y;
            });
        });
        this.force.start();
        this.display_labels();
    };
    BubbleChart.prototype.move_towards_group_center = function(alpha) {
        var _this = this;
        return function(d) {
            var target, value;
            value = d.original[_this.what_to_group_by];
            target = _this.group_centers[value];
            if (typeof target == 'undefined') return;
            d.x = d.x + (target.x - d.x) * (_this.damper + 1) * alpha * 1;
            d.y = d.y + (target.y - d.y) * (_this.damper + 0.09) * alpha * 1.1;
        };
    };
    BubbleChart.prototype.move_towards_group = function(alpha) {
        var _this = this;
        return function(d) {
            var target;
            target = _this.group_centers[d.group];
            d.x = d.x + (target.x - d.x) * (_this.damper + 0.7) * alpha * 1.1;
            d.y = d.y + (target.y - d.y) * (_this.damper + 0.7) * alpha * 1.1;
        };
    };


    BubbleChart.prototype.display_labels = function() {
        var label_data, labels,
            _this = this;
        var group_labels = this.group_labels;
        _this.hide_labels();
        label_data = d3.keys(group_labels);
        //console.log(group_labels);
        labels = this.vis.selectAll(".top_labels").data(label_data);
        labels.enter().append("text").attr("class", "top_labels").attr("width", 80).attr("x", function(d) {
            return group_labels[d];
        }).attr("y", -160).text(function(d) {
            return d;
        });
    };

    BubbleChart.prototype.hide_labels = function() {
        var labels;
        labels = this.vis.selectAll(".top_labels").remove();
    };

    BubbleChart.prototype.show_details = function(data, i, element) {
        var content, key, title, value, _ref;
       // console.log(data);
        d3.select(element)./*attr("stroke", "black").*/style("fill-opacity", 0.85).style("cursor", "pointer");
         content = "Nombre del contrato:<br><b>"+data.original.Titulo+"</b></br>"; 
        content =content+ "Tipo de procedimiento:<br><b>"+data.original.Procedimiento+"</b></br>"; //.Elemento;
         content =content+ "Tipo de Contrato:<br><b>"+data.original.Tipo+"</b></br>"; //.Elemento;
          content = content+"Cuantia adjudicada:<br><b>"+Number(parseFloat(data.original.Monto)).toLocaleString("es-ES", {minimumFractionDigits: 2})+" €</b></br>"; //.Elemento;
        //content = data.original.identifier_legalname; //.Elemento;
        this.tooltip.showTooltip(content, d3.event);
    };

    BubbleChart.prototype.hide_details = function(data, i, element) {
        d3.select(element)./*attr("stroke", "#404040").*/style("fill-opacity", 0.55);
        this.tooltip.hideTooltip();
    };

    BubbleChart.prototype.use_filters = function(filters, targets) {
        var filteredCircles = this.nodes.filter(function(d) {
            var original = d.original;
            var flag = true;
            d.radius = d.value;
            for (var i = 0, len = filters.length; i < len; i++) {
                if (original[targets[i]] === filters[i]) {
                    d.radius = 0;
                    flag = false;
                    break;
                }
            }
            return flag;
        });
        this.do_filter();        
    };

    BubbleChart.prototype.do_filter = function() {
        this.force.start();
        this.circles.transition().duration(2000).attr("r", function(d) {
            return d.radius
        });
    };

    BubbleChart.prototype.getRadius = function(node) {
        return node.Monto;        
    };

    return BubbleChart;
})();
root = typeof exports !== "undefined" && exports !== null ? exports : this;


$(function() {
    var chart, render_chart, render_vis,
        _this = this;
    chart = null;

    render_vis = function(csv) {   

        render_chart(csv);
    };

    render_chart = function(csv) {
        chart = new BubbleChart(csv);
        chart.start();
        root.display_all();
    };

    root.display_all = function() {
        chart.display_group_all();
    };

    root.group_by = function(groupBy) {
        if (groupBy === '') {
            chart.display_group_all();
        } else {
            chart.group_by(groupBy);
        }
    };

    root.color_by = function(colorBy) {
        if (colorBy === '') {
            chart.remove_colors();
        } else {
            chart.color_by(colorBy);
        }
    };

    root.use_filters = function(filters, targets) {
        chart.use_filters(filters, targets);
    };

    root.display_labels = function() {
        chart.display_labels();
    };
    $('#group-everything-by').change(function(){
         return chart.buscar();
    });
     var dato=$("input[id^='indicadorBubble']");
     licitadores3=Array();      
    if(dato.length){
        //console.log(dato);
       
          for(var i=0;i < dato.length;i=i+6){
          if(dato[i] != undefined){
           licitadores3.push(
            {
                "Contrato":$(dato[i]).val(),"y":parseInt($(dato[i]).val()),
                "Empresa":$(dato[i+1]).val(),
                "Cuantia":parseFloat($(dato[i+2]).val()),  
                "Tipo":$(dato[i+3]).val(),              
                "Procedimiento":$(dato[i+4]).val(),                
                "Titulo":$(dato[i+5]).val(),                
            }
           );
          }
        }
    }  
    var data2 = [];
      for (i = 0; i < licitadores3.length; i++) {
        data2.push({
          "Tipo": licitadores3[i].Tipo,
          "Procedimiento":licitadores3[i].Procedimiento,  
          "Monto": licitadores3[i].Cuantia > 0 ? licitadores3[i].Cuantia :0.5,
          "Contrato":licitadores3[i].Contrato,
          "Empresa":licitadores3[i].Empresa,
          "Titulo":licitadores3[i].Titulo,
        });
      }      
       render_filters_colors_and_groups(data2);
      render_vis(data2);   
      
});

function CustomTooltip(tooltipId, width){
    var tooltipId = tooltipId;
    $("body").append("<div class='tooltipBubble' id='"+tooltipId+"'></div>");


    if(width){
        $("#"+tooltipId).css("width", width);
    }

    hideTooltip();

    function showTooltip(content, event){
        $("#"+tooltipId).html(content);
        $("#"+tooltipId).show();

        updatePosition(event);
    }

    function hideTooltip(){
        $("#"+tooltipId).hide();
    }

    function updatePosition(event){
        var ttid = "#"+tooltipId;
        var xOffset = 20;
        var yOffset = 10;

        var ttw = $(ttid).width();
        var tth = $(ttid).height();
        var wscrY = $(window).scrollTop();
        var wscrX = $(window).scrollLeft();
        var curX = (document.all) ? event.clientX + wscrX : event.pageX;
        var curY = (document.all) ? event.clientY + wscrY : event.pageY;
        var ttleft = ((curX - wscrX + xOffset*2 + ttw) > $(window).width()) ? curX - ttw - xOffset*2 : curX + xOffset;
        if (ttleft < wscrX + xOffset){
            ttleft = wscrX + xOffset;
        }
        var tttop = ((curY - wscrY + yOffset*2 + tth) > $(window).height()) ? curY - tth - yOffset*2 : curY + yOffset;
        if (tttop < wscrY + yOffset){
            tttop = curY + yOffset;
        }
        $(ttid).css('top', tttop + 'px').css('left', ttleft + 'px').css('position','absolute');
    }

    return {
        showTooltip: showTooltip,
        hideTooltip: hideTooltip,
        updatePosition: updatePosition
    }
}



var initialise_form = function(selectionOptions) {
    /*var filterers = $('.filter_block input');
    filterers.change(function() {
        var filters = [];
        var targets = [];
        filterers.filter(function() {
            return !this.checked
        }).each(function(k, v) {
            filters[k] = v.value;
            targets[k] = $(v).data('target');
        });
        use_filters(filters, targets);
    });*/

    var groupSelect = $('#group-everything-by');
    for (var opt in selectionOptions) {
        var lookup = selectionOptions[opt];
        groupSelect.append('<option value="' + lookup.key + '">' + lookup.title + '</option>');
        
    }
    var ResetGrouping = function() {
        var groupBy = groupSelect.val();        
        group_by(groupBy);
    };
    groupSelect.change(ResetGrouping);
    var colorSelect = $('#color-everything-by');
    for (var opt in selectionOptions) {
        var lookup = selectionOptions[opt];
        colorSelect.append('<option value="' + lookup.key + '">' + lookup.title + '</option>');
    }
    var ResetColors = function() {
        var colorBy = colorSelect.val();
        color_by(colorBy);
    };   
    colorSelect.change(ResetColors);};


function get_distinct_values(data, keyType, key) {
    var allValues = {};
    for (var i in data) {
        var value = data[i][key];
        allValues[value] = true;
    }
    var allValuesArray = [];
    for (var i in allValues) allValuesArray.push(i);
    allValuesArray.sort();
    return allValuesArray
}

function keyToLookup(key) {
    if(key!=undefined){
    var firstPartEnds = key.indexOf(':');
    if (firstPartEnds <= 0) return {
        key: key,
        type: key,
        title: key
    };
    var firstPart = key.substring(0, firstPartEnds);
    var secondPart = key.substring(firstPartEnds + 1);
    return {
        key: key,
        type: firstPart,
        title: secondPart
    };
}else{
    return{
    key:"",
    type:"",
    title:""};
}
}

function render_filters_colors_and_groups(data) {
    var first = data[0];
   // console.log(first);
    var lookups = [];
    for (var key in first) {
        var lookup = keyToLookup(key);
        if (lookup.type == "Procedimiento" ||  lookup.type =="Tipo"){
            lookups.push(lookup);
        }

    }
   
    initialise_form(lookups);
}

function hide_color_chart() {
    var colorbar = $('#color-hints');
    colorbar.fadeOut(500, function() {
        $(this).empty();
    });
}

function show_color_chart(what_to_color_by, color_mapper) {
    var colorbar = $('#color-hints');     
    //console.log(what_to_color_by);
    
    colorbar.fadeOut(500, function() { 
    colorbar.empty();  
        if(what_to_color_by != undefined){    
        var lookup = keyToLookup(what_to_color_by);
        $('<h4>' + lookup.title + ':</h4>').appendTo(colorbar);
        var row = $('<div class="row" />');
        for (var key in color_mapper) {
			if(lookup.title==='Procedimiento'){
				id=key.replace(/ /g, "");
				dato=$('#procedimiento'+id).text();
				console.log('#procedimiento'+id+'--->'+dato);
			}
			if(lookup.title==='Tipo'){
				id=key.replace(/ /g, "");
				dato=$('#tipo'+id).text();
				console.log('#tipo'+id+'--->'+dato);
			}
            var cell = $('<div class="col-md-3" />');
           if(dato) {var square = $('<div style="width: 15px; height: 15px; background: ' + color_mapper[key] + ';  display: inline-block; vertical-align: middle;">&nbsp;</div><p style="display: inline;"> '+ key +'  '+dato+' % </p>');}
           else var square = $('<div style="width: 15px; height: 15px; background: ' + color_mapper[key] + ';  display: inline-block; vertical-align: middle;">&nbsp;</div><p style="display: inline;"> '+ key +'</p>');
            square.appendTo(cell);
            cell.appendTo(row);
            cell.appendTo(row);
        }
        row.appendTo(colorbar);
        colorbar.fadeIn(500);
    }
    });

}
