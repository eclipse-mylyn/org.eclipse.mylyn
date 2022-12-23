class hudson {

  $base = "/home/tools/hudson"
  $userOwner = "tools"
  $userGroup = "tools"

  exec { "apt-get update":
    command => "apt-get update",
    onlyif  => "find /var/lib/apt/lists/ -mtime -7 | (grep -q Package; [ $? != 0 ])",
  }

  $requirements = [ "apache2", "git-core", ]

  package { $requirements:
    ensure  => "installed",
    require => Exec["apt-get update"],
  }

  service { "apache2":
    ensure  => running,
    require => Package["apache2"],
  }

  exec { "Enable auth_digest module":
    command => "a2enmod auth_digest",
    require => Package["apache2"],
    creates => "/etc/apache2/mods-enabled/auth_digest.load",
    notify  => Service["apache2"],
  }

  exec { "Enable proxy mod":
    command => "a2enmod proxy",
    require => Package["apache2"],
    creates => "/etc/apache2/mods-enabled/proxy.load",
    notify  => Service["apache2"],
  }

  exec { "Enable proxy_http mod":
    command => "a2enmod proxy_http",
    require => Package["apache2"],
    creates => "/etc/apache2/mods-enabled/proxy_http.load",
    notify  => Service["apache2"],
  }

  exec { "Enable ssl module":
    command => "a2enmod ssl",
    require => Package["apache2"],
    creates => "/etc/apache2/mods-enabled/ssl.load",
    notify  => Service["apache2"],
  }

  file { "/etc/apache2/sites-enabled/001-default-ssl":
    ensure  => link,
    target  => "/etc/apache2/sites-available/default-ssl",
    require => Exec["Enable ssl module"],
  }

  file { "/etc/apache2/conf-enabled/proxy.conf":
    source  => "puppet:/modules/hudson/proxy.conf",
    require => Package["apache2"],
    notify  => Service["apache2"],
  }

  exec { "prepare hudson":
    command => "echo Hudson pre-requisites are installed",
    require => Package[$requirements],
  }

}
