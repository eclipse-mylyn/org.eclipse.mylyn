class gerrit {
  $base = "/home/tools/gerrit"
  $userOwner = "tools"
  $userGroup = "tools"

  $_exists =inline_template("<%= File.exists?('/etc/gerrit_clear_mode') %>")
  if $_exists == "true"  {
    $clearMode            = regsubst(file("/etc/gerrit_clear_mode"), '\n', '')
  } else {
    $clearMode            = "noclear"
    exec { "create clearMode":
      command => "echo \"noclear\" >/etc/gerrit_clear_mode",
      creates => '/etc/gerrit_clear_mode',
    }
  }

  exec { "apt-get update":
    command => "apt-get update",
    onlyif  => "find /var/lib/apt/lists/ -mtime -7 | (grep -q Package; [ $? != 0 ])",
  }

  $requirements = ["apache2", "git-core",]

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

  exec { "Enable proxy mod":
    command => "a2enmod proxy",
    require => Package["apache2"],
    creates => "/etc/apache2/mods-enabled/proxy.load",
  }

  exec { "Enable proxy_http mod":
    command => "a2enmod proxy_http",
    require => Package["apache2"],
    creates => "/etc/apache2/mods-enabled/proxy_http.load",
  }

  exec { "Enable ssl module":
    command => "a2enmod ssl",
    require => Package["apache2"],
    creates => "/etc/apache2/mods-enabled/ssl.load",
  }

  file { "/etc/apache2/sites-enabled/001-default-ssl":
    ensure  => link,
    target  => "/etc/apache2/sites-available/default-ssl",
    require => Exec["Enable ssl module"],
  }

  file { "/etc/apache2/conf-enabled/proxy.conf":
    source  => "puppet:/modules/gerrit/proxy.conf",
    require => Package["apache2"],
    notify  => Service["apache2"],
  }

  exec { "prepare gerrit":
    command => "echo Gerrit pre-requisites are installed",
    require => Package[$requirements],
  }

  file { "/usr/lib/cgi-bin/services":
    source  => "puppet:///modules/gerrit/services.cgi",
    mode    => 755,
    require => Package[$requirements],
  }

}