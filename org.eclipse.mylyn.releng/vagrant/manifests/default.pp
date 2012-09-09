Exec { path => [ "/bin/", "/sbin/" , "/usr/bin/", "/usr/sbin/" ] }

class apache {
  package { "apache2":
    ensure => present,
  }

  service { "apache2":
    ensure => running,
    require => Package["apache2"],
  }
}

exec { "Enable proxy mod":
    command => "a2enmod proxy",
    require => Package["apache2"],
}

exec { "Enable proxy_http mod":
    command => "a2enmod proxy_http",
    require => Package["apache2"],
}

include apache

exec { "apt-get update":
    command => "apt-get update",
}

package { "openjdk-6-jre":
    ensure => "installed",
    require  => Exec["apt-get update"],
}
