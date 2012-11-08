Exec { path => [ "/bin/", "/sbin/" , "/usr/bin/", "/usr/sbin/" ] }  

include "trac"

trac::defaultsites { "trac":
}
