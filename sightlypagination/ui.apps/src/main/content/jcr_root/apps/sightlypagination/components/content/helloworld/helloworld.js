"use strict";
 
use(function () {

    log.info("-----------------------  "+this.matches);

    var m = this.matches;
    var offset, p = "", n,pages;
   	var selectors = new Array();

    var pages = new Array();
    selectors = request.getRequestPathInfo().getSelectors();

    // Getting Selector
    if(selectors.length!=0)
        var sel = selectors[0];
   	else 
        var sel = 0;

    var maxSel = m/5+1;
    maxSel = Math.floor(maxSel-1);
	// Previous Link
    if(sel!=0 && sel < m/5+1){
		p = parseInt(sel) - 1;
	} 

    // Next link
    if((sel+1)*5 > m)  {
		offset = 0;
        n="";
    } else {
        n = parseInt(sel) + 1;
    }

    // Adjacent links
    var fcount = 0;
    var bcount = 0;
    var flimit = 2;
    var blimit = 2;
    log.info("Max Selector :" + maxSel);

    if(maxSel-sel >= 2) {
		var blimit = 2;
    } else {
        flimit = flimit + maxSel - sel;
        blimit = maxSel-sel;
    }

    if(sel-0 < 2) {
        blimit = blimit + parseInt(sel)-0;
    }
    log.info("Blimit:"+blimit);
    log.info("Flimit:"+flimit);
    // Front 
    for(i = sel-1; i>=0; i--) {
        if(fcount!=flimit ) {
           // log.info(fcount);
            pages[fcount] = i;
           	log.info(fcount + "-"+i);
            fcount++;
        }
    }

    for(i = parseInt(sel)+1; i<=maxSel; i++) {
        log.info("i="+i);
		if(bcount!=blimit) {
            pages[bcount+fcount] = i;
           	log.info(bcount+fcount + "-"+i);
            bcount++;
        }
    }

    log.info(pages.length)


    pages[pages.length] = sel;
    pages.sort();

    var pageList = new Array();

    for(var n =0;n<pages.length;n++) {
      var x  = parseInt(pages[n]) + 1;
      pages[n] = x.toString();
    }



    return {
        previous: p.toString(),
        next: n.toString(),
        pages: pages
    }; 


});