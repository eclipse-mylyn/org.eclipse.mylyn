Exec {
  path => ["/bin/", "/sbin/", "/usr/bin/", "/usr/sbin/"] }

include "trac"

user { "tools":
  ensure     => present,
  membership => minimum,
  shell      => "/bin/bash",
  managehome => true,
}

trac::defaultsites { "trac":
}
