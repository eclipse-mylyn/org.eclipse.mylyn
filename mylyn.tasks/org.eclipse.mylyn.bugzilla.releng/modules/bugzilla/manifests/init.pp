/*******************************************************************************
 * Copyright (c) 2012 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
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
    "apache2-mpm-prefork",
    "apache2",
    "build-essential",
    "emacs",
    "git-core",
    "language-pack-en",
    "libapache2-mod-auth-mysql",
    "libapache2-mod-fcgid",
    "libapache2-mod-perl2-dev",
    "libapache2-mod-perl2",
    "libapache2-mod-php5",
    "libappconfig-perl",
    "libauthen-radius-perl",
    "libauthen-sasl-perl",
    "libcgi-pm-perl",
    "libchart-perl",
    "libdaemon-generic-perl",
    "libdate-calc-perl",
    "libdatetime-perl",
    "libdatetime-timezone-perl",
    "libdbd-mysql-perl",
    "libdbi-perl",
    "libemail-mime-modifier-perl",
    "libemail-mime-perl",
    "libemail-send-perl",
    "libencode-detect-perl",
    "libfile-slurp-perl",
    "libgd-graph-perl",
    "libhtml-scrubber-perl",
    "libjson-rpc-perl",
    "libmail-sendmail-perl",
    "libmath-random-isaac-perl",
    "libmath-random-isaac-xs-perl",
    "libmime-perl",
    "libmodule-build-perl",
    "libnet-ldap-perl",
    "libsoap-lite-perl",
    "libtemplate-perl-doc",
    "libtemplate-perl",
    "libtemplate-plugin-gd-perl",
    "libtest-taint-perl",
    "libtheschwartz-perl",
    "libxml-perl",
    "libxml-twig-perl",
    "make",
    "mysql-server",
    "patchutils",
    "perl-doc",
    "perlmagick",
    "php5-mysql",
    "php5",
    "phpmyadmin",
    "puppet",
    "cpanminus"
    ]
    

  package { $requirements:
    ensure  => "installed",
    require => Exec["apt-get update"],
  }

  service { "apache2":
    ensure  => running,
    require => Package["apache2"],
  }
 
  exec { "Enable php5 module":
    command => "a2enmod php5",
    require => Package["libapache2-mod-php5"],
    creates => "/etc/apache2/mods-enabled/php5.load",
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
  
  exec { "Enable rewrite module":
    command => "a2enmod rewrite",
    require => Package["apache2"],
    creates => "/etc/apache2/mods-enabled/rewrite.load",
  }
  
  exec { "Enable cgi module":
    command => "a2enmod cgi",
    require => Package["apache2"],
    creates => "/etc/apache2/mods-enabled/cgi.load",
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

/* lvps92-51-163-75.dedicated.hosteurope.de is the actual hostname for mylyn.org 
   so we not show the mysql admin for that suite 
*/
 if $envhost != "lvps92-51-163-75.dedicated.hosteurope.de"{
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
    command => "cpanm CGI; cpanm DBI; cpanm DateTime::TimeZone; cpanm Email::Sender;echo Bugzilla pre-requisites are installed",
    require => [Exec["mysql create user ${dbuser}"],Exec["create  $confDir"]],
  }

}