/*******************************************************************************
 * Copyright (c) 2012 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *     Steffen Pingel (Tasktop Techologies)
 *******************************************************************************/
class bugzilla {
  $userOwner = "tools"
  $userGroup = "tools"
  $bugzillaBase = "/home/$userOwner/bugzilla"
  $dbuser = 'bugz'
  $dbuserPassword = 'ovlwq8'
  $envhost = regsubst(file("/etc/hostname"), '\n', '')
  $confDir = "$bugzillaBase/conf.d"

  exec { "apt-get update":
    command => "apt-get update",
    onlyif  => "find /var/lib/apt/lists/ -mtime -7 | (grep -q Package; [ $? != 0 ])",
  }

  $requirements = [
    "apache2",
    "apache2.2-common",
    "libapache2-mod-auth-mysql",
    "libapache2-mod-fcgid",
    "libapache2-mod-php5",
    "mysql-server",
    "make",
    "perl-doc",
    "php5",
    "php5-mysql",
    "phpmyadmin",
    "libcgi-pm-perl",
    "libdbd-mysql-perl",
    "libdatetime-perl",
    "libdatetime-timezone-perl",
    "libemail-mime-perl",
    "libemail-send-perl",
    "libjson-rpc-perl",
    "libmail-sendmail-perl",
    "libmath-random-isaac-perl",
    "libtest-taint-perl",
    "liburi-perl",
    "libsoap-lite-perl",
    "libtemplate-perl",
    "patchutils",
    "git",
    ]

  package { $requirements:
    ensure  => "installed",
    require => Exec["apt-get update"],
  }

  exec { "Enable php5 module":
    command => "a2enmod php5",
    require => Package["libapache2-mod-php5"],
    creates => "/etc/apache2/mods-enabled/php5.load",
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

  service { "mysql":
    ensure  => "running",
    enable  => true,
    require => Package["mysql-server"],
  }

  exec { "phpmyadmin_Apache2":
    command => "echo '#phpmyadmin\nInclude /etc/phpmyadmin/apache.conf' >>/etc/apache2/apache2.conf",
    require => [Package["phpmyadmin"], Package["libapache2-mod-php5"]],
    unless  => 'cat /etc/apache2/apache2.conf | grep "#phpmyadmin"'
  }

  file { "/etc/apache2/sites-enabled/001-default-ssl":
    ensure => link,
    target => "/etc/apache2/sites-available/default-ssl",
  }

 if $envhost != "mylyn.org"{
    file { "$bugzillaBase/servicephpmyadmin.json":
      source  => "puppet:///modules/bugzilla/servicephpmyadmin.json",		
      owner   => "$userOwner",
      group   => "$userGroup",
      mode    => 644,
      require => Package[$requirements],
    }
  }

  file { "/usr/lib/cgi-bin/services":		
    source  => "puppet:///modules/bugzilla/services.cgi",		
    mode => 755,		
    require => Package[$requirements],
  }

  $_exists =inline_template("<%= File.exists?('/etc/bugzilla_clear_mode') %>")
  if $_exists == "true"  {
    $clearMode            = regsubst(file("/etc/bugzilla_clear_mode"), '\n', '')
  } else {
    $clearMode            = "noclear"
    exec { "create clearMode":
      command => "echo \"noclear\" >/etc/bugzilla_clear_mode",
      creates => '/etc/bugzilla_clear_mode',
    }
  }

  exec { "create  $confDir":
    command => "mkdir -p $confDir",
    creates => "$confDir",
    user => "$userOwner",
    require => Package[$requirements],
  }

  exec { "mysql create user ${dbuser}":
    unless   => "/usr/bin/mysql --user='${dbuser}' --password='${dbuserPassword}'",
    command   => "/usr/bin/mysql -v --user='root' -e \"CREATE USER '${dbuser}'@localhost IDENTIFIED BY '${dbuserPassword}'\"",
    logoutput => true,
    require   => Package["mysql-server"],
  }

  exec { "prepare bugzilla":
    command => "echo Bugzilla pre-requisites are installed",
    require => [Exec["mysql create user ${dbuser}"],Exec["create  $confDir"]],
  }

}
