Exec { path => [ "/bin/", "/sbin/" , "/usr/bin/", "/usr/sbin/" ] }  

include "hudson"

user { "tools":
        ensure => present,
        membership => minimum,
        shell => "/bin/bash",
        managehome => 'true',
}

hudson::defaultsites { "hudson":
}
