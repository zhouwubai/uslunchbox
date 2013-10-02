/*!
* LICENSE: You are welcome to use this plugin, redistribute
* as needed and modified as needed. If you choose to do any
* of the above this comment section including copyright must 
* remain in place. If you modify the plugin great. Include under
* the "Modified by:" section your name and contact information.
* This aids in expanding the plugin and gives reference to those
* that have contributed, in turn making it easy to further improve
* upon the plugin...enjoy!
*
* quickbar 1.0
* http://www.origin1.com
*
* Developed by: 
* - Carey Hazelton  http://www.origin1.com
*                   mailto:origin1tech@gmail.com
* Modified by:
* 
*
* Copyright (c) 2009-2012
*/

(function ($) {

    //Global variables.
    var options, methods, ul;
  
    function isDefined(obj, defVal) {
        if (obj == undefined || obj == false)
            return defVal;
        return obj;
    };

    //Defing options/methods.
    options, methods = {
        init: function (overrides, callback) {
            options = {
                styles: { container: 'quickbar-container', bar: 'quickbar-bar', title: 'quickbar-title', label: 'quickbar-label' },
                title: { visible: true, width: 50, position: 'left' }, // position options are "top", "left" or "bottom". width is required.
                label: { visible: true, width: 50, position: 'right' }, // position options are "right" or "inner". width is required.
                refresh: { btn: '.quickbar-refresh', method: '' }, // enter custom method else will use options.data.
                data: [],
                animate: true
            }

            //Merge options/overrides.
            var styleOpt, titleOpt, labelOpt, refreshOpt;
            stylesOpt = $.extend({}, options.styles, overrides.styles);
            titleOpt = $.extend({}, options.title, overrides.title);
            labelOpt = $.extend({}, options.label, overrides.label);
            refreshOpt = $.extend({}, options.refresh, overrides.refresh);

            options = $.extend({}, options, overrides);

            options.styles = stylesOpt;
            options.title = titleOpt;
            options.label = labelOpt;
            options.refresh = refreshOpt;

            ul = isDefined(ul, this);

            return ul.each(function () {

                //Declare variables.            
                var ulWidth, hasData;
                ulWidth = ul.outerWidth();
                hasData = ul.children('li').size() > 0 ? true : false; // indicates if data-dash li items defined.       

                if (hasData) {
                    options.data = [];
                    ul.children('li').each(function () {
                        options.data.push({
                            title: isDefined($(this).attr('data-quickbar-title'), 'None'),
                            value: isDefined($(this).attr('data-quickbar-value'), 0),
                            label: isDefined($(this).attr('data-quickbar-label'), 'None'),
                            width: isDefined($(this).attr('data-quickbar-width'), 0),
                            hasData: true
                        });
                    });
                }

                // check if data is a function if is call.              
                if (typeof (options.data) == 'function')
                    options.data = options.data.call(this);

                // make sure we have data.
                if (isDefined(options.data, 0) == 0) {
                    console.log('Exiting quickbar no data to init.');
                    return;
                }

                // iterate through the data.            
                $.each(options.data, function (i, row) {

                    var titleTxt, valueInt, labelTxt, maxWidth,
                        barWidth, li, container, bar, title, offsetWidth;
                    li = null;
                    offsetWidth = 0;

                    //Define the line element.
                    li = (hasData == true) ? ul.children('li:eq(' + i + ')') : $('<li></li>');
                    li.css('clear', 'both');

                    // Create element templates.
                    container = $('<div class="' + options.styles.container + '"></div>');
                    bar = $('<div class="' + options.styles.bar + '">&nbsp;</div>');
                    title = $('<div class="' + options.styles.title + '"></div>');
                    label = $('<div class="' + options.styles.label + '"></div>');

                    container.width(ulWidth + 'px'); // set the default container width;
                    container.css('float', 'left');
                    bar.width('0px'); // make sure the bar is initiall set to 0 width.                       
                    bar.css('float', 'left');

                    titleTxt = row.title;
                    valueInt = row.value;
                    labelTxt = row.label;
                    maxWidth = row.width;

                    // parse values for integers.
                    valueInt = parseInt(valueInt, 10);
                    valueInt = valueInt > 100 ? 100 : valueInt; // maximum value is 100.
                    maxWidth = parseInt(maxWidth, 10); // make sure we have integer.

                    if (options.title.visible && options.title.position == 'left')
                        offsetWidth += options.title.width;

                    if (options.label.visible && options.label.position == 'right')
                        offsetWidth += options.label.width;

                    if (maxWidth == 0 || (maxWidth + offsetWidth) > ulWidth)
                        maxWidth = (ulWidth - offsetWidth) - 25;

                    container.width(maxWidth + 'px');

                    // calculate the width of the bar.
                    barWidth = maxWidth * (valueInt / 100);

                    //console.log('Value: ' + valueInt + ' Max Width: ' + maxWidth + ' Bar Width: ' + barWidth);

                    // Add jquery data to element if data exists.                   
                    li.data({ title: titleTxt, value: valueInt, label: labelTxt, width: maxWidth, hasData: hasData });

                    // add the bar to the container.
                    container.append(bar);
                    li.append(container);

                    if (options.label.visible) {
                        label.html(labelTxt);
                        if (options.label.position == 'inner')
                            container.append(label);
                        else
                            li.append(label);
                    }

                    if (options.title.visible) {
                        title.html(titleTxt);
                        title.css('width', options.title.width + 'px');

                        // title is positioned however use css to fine tune
                        // alignment and padding etc.
                        if (options.title.position == 'top')
                            li.prepend(title);

                        if (options.title.position == 'bottom')
                            li.append(title);

                        if (options.title.position == 'left') {
                            title.css('float', 'left');
                            li.prepend(title);
                        }
                    }

                    // if we've passed data as array add to ul.
                    if (!hasData)
                        ul.append(li);

                    if (options.animate)
                        bar.animate({ 'width': barWidth + 'px' });
                    else
                        bar.width(barWidth + 'px');
                });

                // bind refresh options/method.
                if ($(options.refresh.btn).length != 0) {
                    $(options.refresh.btn).bind('click', function () {
                        if (options.refresh.method && typeof (options.refresh.method) == 'function')
                            options.data = options.refresh.method.call(this);                     
                        ul.quickbar('reinit', options);
                    });
                }
                methods.callback(callback);
            });
            // end init.          
        },

        reinit: function (opt, callback) {          
            if (isDefined(opt.data, 0) == 0) {
                console.log('Exiting quickbar no data to init.');
                return;
            }
            ul = isDefined(ul, this);
            methods.destroy();
            methods.init(opt, callback);
        },

        destroy: function (callback) {
            ul = isDefined(ul, this);
            ul.children().each(function () {
                // removes container/bars created by quickbar. 
                if ($(this).data().hasData)
                    $(this).children().remove();
                else
                    $(this).remove();
            });
            if ($(options.refresh.btn).length != 0)
                $(options.refresh.btn).unbind('click');
            methods.callback(callback);
        },

        callback: function (callback) {
            if (callback) callback(ul, options);
        }
    };

    $.fn.quickbar = function (method) {
        if (methods[method]) {
            return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
        } else if (typeof method === 'object' || !method) {
            return methods.init.apply(this, arguments);
        } else {
            $.error('Method ' + method + ' does not exist on jQuery.quickbar');
        }
    };

})(jQuery);