Exec {
  path => ["/bin/", "/sbin/", "/usr/bin/", "/usr/sbin/"] }

user { "tools":
  ensure     => present,
  membership => minimum,
  shell      => "/bin/bash",
  managehome => true,
}

include "trac"

exec { "disable all":
  command => "find $trac::base -name \"service*.json\" | xargs -i mv {} {}.disabled",
  onlyif  => "test -e $trac::base",
}

trac::defaultsites { "trac":
}