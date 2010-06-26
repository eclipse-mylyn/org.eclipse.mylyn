#!/bin/sh
sed -i -e s/:pserver:anonymous@dev.eclipse.org:// maps/*.map
sed -i -e s/http:\\/\\/eclipse.unixheads.org/file:\\/home\\/data\\/httpd\\/download.eclipse.org/ maps/*.map
sed -i -e s/http:\\/\\/download.eclipse.org/file:\\/home\\/data\\/httpd\\/download.eclipse.org/ maps/*.map
