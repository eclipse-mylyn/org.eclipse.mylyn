define gerrit::defaultsites {
  $userOwner = "tools"
  $userGroup = "tools"

exec { "apt-get update":
    command => "apt-get update",
    onlyif => "find /var/lib/apt/lists/ -mtime -7 | (grep -q Package; [ $? != 0 ])",
}

$requirements = [ "openjdk-6-jre", "git-core", ]
package { $requirements: 
	ensure => "installed", 
  require => Exec["apt-get update"],
}

Gerrit {
	require => Package[$requirements],
}

Gerrit::Site {
}

gerrit { "2.4.2":
}

gerrit { "2.5.1":
  postfix => "-full",
}

gerrit::site { "gerrit-2.4":
	version => "2.4.2",
	port => 28242,
	sshport => 29242,
	require => Gerrit["2.4.2"],
}

gerrit::site { "gerrit-2.5":
  version => "2.5.1",
  port => 28251,
  sshport => 29251,
  require => Gerrit["2.5.1"],
}

}