Exec { path => [ "/bin/", "/sbin/" , "/usr/bin/", "/usr/sbin/" ] }  

include "hudson"

hudson::defaultsites { "hudson":
}
