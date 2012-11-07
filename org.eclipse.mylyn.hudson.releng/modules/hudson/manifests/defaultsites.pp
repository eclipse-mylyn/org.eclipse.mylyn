define hudson::defaultsites {

exec { "apt-get update":
    command => "apt-get update",
    onlyif => "find /var/lib/apt/lists/ -mtime -7 | (grep -q Package; [ $? != 0 ])",
}

$requirements = [ "openjdk-6-jre", "git-core", ]
package { $requirements: 
	ensure => "installed", 
  require => Exec["apt-get update"],
}

Hudson {
	require => Package[$requirements],
}

Hudson::Site {
	data => "hudson-2.1",
}

hudson { "2.1.0":
  type => "hudson",
}

hudson { "2.2.1":
  type => "hudson",
}

hudson { "3.0.0-RC4":
  type => "hudson",
  qualifier => "eclipse",
}

hudson { "1.466.2":
  type => "jenkins",
  qualifier => "stable",
}

hudson { "1.489":
  type => "jenkins",
}

hudson::site { "hudson-2.1":
  envtype => "hudson",
  version => "2.1.0",
  port => 9010,
	require => Hudson["2.1.0"],
}

hudson::site { "hudson-2.2":
  envtype => "hudson",
  version => "2.2.1",
  port => 9020,
  require => Hudson["2.2.1"],
}

hudson::site { "hudson-3.0":
  envtype => "hudson",
  version => "3.0.0-RC4",
  port => 9030,
  require => Hudson["3.0.0-RC4"],
}

hudson::site { "jenkins-1.466":
  envtype => "jenkins",
  version => "1.466.2",
  port => 9101,
  require => Hudson["1.466.2"],
}

hudson::site { "jenkins-1.489":
  envtype => "jenkins",
  version => "1.489",
  port => 9110,
  require => Hudson["1.489"],
}

}