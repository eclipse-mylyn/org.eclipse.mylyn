#!/usr/bin/env python3
# -*- coding: UTF-8 -*-
 
import os
import sys
import json
import socket
import psutil
import requests
 
from flask import Flask, request
 
VISITS = 0
 
app = Flask(__name__)
 
 
def getsocket(pid):
 
    for c in psutil.net_connections(kind='inet'):
 
        if c.pid == int(pid) and c.status == 'ESTABLISHED':
 
            return "%s:%s" % (c.laddr)
 
 
@app.route("/service")
def serviceJson():
 
    global VISITS
 
    VISITS += 1
    
    debugMode = request.args.get('debugMode', default = 0, type = int)
    
    r=requests.get('http://docker-socket-proxy:2375/containers/json',verify=False)
    js = json.loads(r.text) 
    result = "["
    for i in js:
      xx = i['Labels']
      if "org.eclipse.mylyn.service" in xx:
        if len(result) > 1:
          result += ", "
        result += xx['org.eclipse.mylyn.service']
    result += "]"    

    if 'callback' in request.args:
        result = request.args.get('callback', type = str) + "("+ result + ")"
 
    html = "<html><head><title>Mylyn Index</title></head>" \
        "<style>" \
        "body {" \
        "background-color: white;" \
        "text-align: left;" \
        "padding: 50px;" \
        "font-family: 'Open Sans','Helvetica Neue',Helvetica,Arial,sans-serif;" \
        "}" \
        "</style>" \
        "</head>" \
        "<body>"
 
    html += "<h2>Mylyn Index from <font color='#337ab7'>{name}</font></h2>".format(name=request.host)
    html += "<b>Backend hostname:</b> {hostname}<br>".format(hostname=socket.gethostname())
    html += "<b>Backend socket:</b> {socket}<br>".format(socket=getsocket(os.getpid()))
    html += "<br>"
 
    html += "<b>Visits:</b> {visits}<br>".format(visits=VISITS)
    html += "<br>"
    
        
    if request.url:
 
        html += "<b>Request Url: </b>" + str(request.url) + "<br>"
        html += "<b>Request Path: </b>" + str(request.path) + "<br>"
        html += "<b>Request Method: </b>" + str(request.method) + "<br>"
        html += "<br><hr>"
 
    if request.headers:
 
        html += "<h4>Headers:</h4>"
 
        for headers_key, headers_value in request.headers.items():
 
            html += headers_key + ": " + headers_value + "<br>"
    if debugMode > 0: 
        html += "<br><hr>Debugtext<br>"

        for i in js:
          html += "<br><br><b>"
          html += str(i['Names'])
          html += "</b>  -> "
          xx = i['Labels']
          if "org.eclipse.mylyn.service" in xx:
            html += str(i['Labels'])
        html += "<br><br>"

    if debugMode == 0:
        html = result
    else:
        html += "<br><hr>"
        html += result
        html += "<br><hr><br>{MODE}".format(MODE=debugMode)
 
    return html

@app.route("/")
@app.route("/index.html")
def tableView():
    html = "<html><head><title>Mylyn Service Index</title></head>" \
        "<style>" \
        "body {" \
        "background-color: white;" \
        "text-align: left;" \
        "padding: 50px;" \
        "font-family: 'Open Sans','Helvetica Neue',Helvetica,Arial,sans-serif;" \
        "}" \
        "</style>" \
        "</head>" \
        "<body>"
    body = """<blockquote>
  <script type="text/javascript">
  <!-- to hide script contents from old browsers
    function insertReply(content) {
      var erg = "<table width=\\"100%\\" border=\\"1\\"><tbody>";
      erg = erg + "<tr><td>Kind</td><td>URL</td><td>Version</td><td>Info</td><td>Properties</td></tr>";
      for (var i=0,len=content.length; i<len; i++) {
        erg = erg + "<tr><td>" + content[i].type
            + "</td><td><a href=\\"" + content[i].url
            + "\\">"+ content[i].url + "</a></td><td>" + content[i].version+ "</td><td>"
            + content[i].info + "</td><td>";
        var properties = content[i].properties;
        if(!properties) {
          erg = erg + " ";
        } else {
          var keys = Object.keys(properties);
          for (var j=0,lenkeys=keys.length; j<lenkeys; j++) {
            erg = erg + keys[j]+ " = " + properties[keys[j]]  +"<br>";
          }
        }
        erg = erg + "</td></tr>";
      }
      erg = erg + "</tbody></table>";
      document.getElementById('output').innerHTML = erg;
    }
    // create script element
    var script = document.createElement('script');
    // assing src with callback name
    script.src = './service?callback=insertReply';
    // insert script to document and load content
    document.body.appendChild(script);
    // end hiding contents from old browsers  -->
  </script> </blockquote>
  <a id="output">Retrieving services list...</a>
</body>
</html>"""

    html += "<h2>Mylyn Service Index from <font color='#337ab7'>{name}</font></h2>".format(name=request.host)
    html += "<b>Backend hostname:</b> {hostname}<br>".format(hostname=socket.gethostname())
    html += "<b>Backend socket:</b> {socket}<br>".format(socket=getsocket(os.getpid()))
    html += "<br>"
    if request.url:
 
        html += "<b>Request Url: </b>" + str(request.url) + "<br>"
        html += "<b>Request Path: </b>" + str(request.path) + "<br>"
        html += "<b>Request Method: </b>" + str(request.method) + "<br>"
        html += "<br><hr>"
 
    if request.headers:
 
        html += "<h4>Headers:</h4>"
 
        for headers_key, headers_value in request.headers.items():
 
            html += headers_key + ": " + headers_value + "<br>"
    html += "<b>Visits:</b> {visits}<br>".format(visits=VISITS)
    html += "<br>"
    html += body
 
    return html
 
 
if __name__ == "__main__":
 
    if len(sys.argv) == 2:
        app.run(host='0.0.0.0', port=int(sys.argv[1]), debug=True)
 
    else:
        app.run(host='0.0.0.0', debug=True)
