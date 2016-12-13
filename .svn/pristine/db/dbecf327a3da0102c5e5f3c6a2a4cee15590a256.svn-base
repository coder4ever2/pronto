function loadScript(url, callback) {
    // Adding the script tag to the head as suggested before
    var head = document.getElementsByTagName('head')[0];
    var script = document.createElement('script');
    script.type = 'text/javascript';
    script.src = url;

    // Then bind the event to the callback function.
    // There are several events for cross browser compatibility.
    script.onreadystatechange = callback;
    script.onload = callback;

    // Fire the loading
    head.appendChild(script);
}

function loadjscssfile(filename, filetype) {
    if (filetype == "js") { //if filename is a external JavaScript file
        var fileref = document.createElement('script')
        fileref.setAttribute("type", "text/javascript")
        fileref.setAttribute("src", filename)
    }
    else if (filetype == "css") { //if filename is an external CSS file
        var fileref = document.createElement("link")
        fileref.setAttribute("rel", "stylesheet")
        fileref.setAttribute("type", "text/css")
        fileref.setAttribute("href", filename)
    }
    if (typeof fileref != "undefined")
        document.getElementsByTagName("head")[0].appendChild(fileref)
}

var dir = document.querySelector('script[src$="customscript.js"]').getAttribute('src');
var name = dir.split('/').pop();
dir = dir.replace('/' + name, "");

<!-- chatjs requirements -->
//loadjscssfile('http://code.jquery.com/jquery-2.1.1.min.js', 'js');
/*loadjscssfile(dir + '/ChatJs/js/jquery.autosize.js', 'js');
loadjscssfile(dir + '/Styles/styles.css', 'css');

<!-- chatjs files-->
loadjscssfile(dir + '/ChatJs/js/jquery.chatjs.utils.js', 'js');
loadjscssfile(dir + '/ChatJs/js/jquery.chatjs.adapter.servertypes.js', 'js');
loadjscssfile(dir + '/ChatJs/js/jquery.chatjs.adapter.js', 'js');
loadjscssfile(dir + '/ChatJs/js/jquery.chatjs.adapter.demo.js', 'js');
loadjscssfile(dir + '/ChatJs/js/jquery.chatjs.window.js', 'js');
loadjscssfile(dir + '/ChatJs/js/jquery.chatjs.messageboard.js', 'js');
loadjscssfile(dir + '/ChatJs/js/jquery.chatjs.userlist.js', 'js');
loadjscssfile(dir + '/ChatJs/js/jquery.chatjs.pmwindow.js', 'js');
loadjscssfile(dir + '/ChatJs/js/jquery.chatjs.friendswindow.js', 'js');
loadjscssfile(dir + '/ChatJs/js/jquery.chatjs.controller.js', 'js');
loadjscssfile(dir + '/ChatJs/css/jquery.chatjs.css', 'css');*/

loadScript('http://code.jquery.com/jquery-2.1.1.min.js', function (params) {

    loadjscssfile(dir + '/ChatJs/js/jquery.autosize.js', 'js');
    loadjscssfile(dir + '/Styles/styles.css', 'css');

    <!-- chatjs files-->
    loadjscssfile(dir + '/ChatJs/js/jquery.chatjs.utils.js', 'js');
    loadjscssfile(dir + '/ChatJs/js/jquery.chatjs.adapter.servertypes.js', 'js');
    loadjscssfile(dir + '/ChatJs/js/jquery.chatjs.adapter.js', 'js');
    loadjscssfile(dir + '/ChatJs/js/jquery.chatjs.adapter.demo.js', 'js');
    loadjscssfile(dir + '/ChatJs/js/jquery.chatjs.window.js', 'js');
    loadjscssfile(dir + '/ChatJs/js/jquery.chatjs.messageboard.js', 'js');
    loadjscssfile(dir + '/ChatJs/js/jquery.chatjs.userlist.js', 'js');
    loadjscssfile(dir + '/ChatJs/js/jquery.chatjs.pmwindow.js', 'js');
    loadjscssfile(dir + '/ChatJs/js/jquery.chatjs.friendswindow.js', 'js');
    loadjscssfile(dir + '/ChatJs/js/jquery.chatjs.controller.js', 'js');
    loadjscssfile(dir + '/ChatJs/css/jquery.chatjs.css', 'css');
});


