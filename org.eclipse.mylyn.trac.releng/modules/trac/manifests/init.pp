class trac {
  $base = "/home/tools/trac"
  $userOwner = "tools"
  $userGroup = "tools"

  /* Common requirements for all Trac instances */

  exec { "apt-get update":
    command => "apt-get update",
    onlyif  => "find /var/lib/apt/lists/ -mtime -7 | (grep -q Package; [ $? != 0 ])",
  }

  /* Trac automatically provisions during install "python-genshi" */

  $requirements = ["apache2", "libapache2-mod-fcgid", "python-pysqlite2", "python-setuptools", "python-subversion", "subversion",]

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
  }

  exec { "Enable fcgid module":
    command => "a2enmod fcgid",
    require => Package["libapache2-mod-fcgid"],
    creates => "/etc/apache2/mods-enabled/fcgid.load",
  }

  exec { "Enable ssl module":
    command => "a2enmod ssl",
    require => Package["apache2"],
    creates => "/etc/apache2/mods-enabled/ssl.load",
  }

  file { "/etc/apache2/sites-enabled/001-default-ssl":
    ensure  => link,
    target  => "/etc/apache2/sites-available/default-ssl",
    require => Package["apache2"],
    notify  => Service["apache2"],
  }

  file { "/etc/apache2/conf.d/python.conf":
    content => "DefaultInitEnv PYTHON_EGG_CACHE /tmp/eggs",
    require => Package["apache2"],
    notify  => Service["apache2"],
  }
  
  exec { "prepare trac":
    command => "echo Trac pre-requisites are installed",
    require => Package[$requirements],
  }
  
  file { "/usr/lib/cgi-bin/services":		
    source  => "puppet:///modules/trac/services.cgi",		
    mode => 755,		
    require => Package[$requirements],
  }
  
}
