Exec {
  path => ["/bin/", "/sbin/", "/usr/bin/", "/usr/sbin/"] }

include "gerrit"

user { "tools":
  ensure     => present,
  membership => minimum,
  shell      => "/bin/bash",
  managehome => true,
}

exec { "stop all":
  command => "find $gerrit::base -name gerrit.sh | xargs -i /bin/sh -c '(cd $(dirname {}) && {} stop)'",
  onlyif  => "test -e $gerrit::base",
}

exec { "disable all":
  command => "find $gerrit::base -name \"service*.json\" | xargs -i mv {} {}.disabled",
  onlyif  => "test -e $gerrit::base",
}

gerrit::defaultsites { "gerrit":
}

