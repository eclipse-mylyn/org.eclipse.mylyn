$content ="#!/bin/bash

echo content-type: application/json
echo

echo '['
find /home/tools -name service.json | xargs cat
echo ']'
"

file { "/usr/lib/cgi-bin/services":
  content => "$content",
  mode => 755,
}
