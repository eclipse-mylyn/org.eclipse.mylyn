Exec {
  path => ["/bin/", "/sbin/", "/usr/bin/", "/usr/sbin/"] }

include "hudson"

user { "tools":
  ensure     => present,
  membership => minimum,
  shell      => "/bin/bash",
  managehome => true,
}

exec { "stop all":
  command => "find $hudson::base -name stop.sh | xargs -i /bin/sh -c '(cd $(dirname {}) && {})'",
  onlyif => "test -e $hudson::base",
}

exec { "disable all":
  command => "find $hudson::base -name \"service*.json\" | xargs -i mv {} {}.disabled",
  onlyif => "test -e $hudson::base",
}

hudson::defaultsites { "hudson":
}
