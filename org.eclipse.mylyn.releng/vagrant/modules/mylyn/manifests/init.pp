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
class mylyn {

  exec { "apt-get update":
    command => "apt-get update",
    onlyif  => "find /var/lib/apt/lists/ -mtime -7 | (grep -q Package; [ $? != 0 ])",
  }

  $requirements = [
    "apache2",
    "libfile-find-rule-perl-perl",
#  for local testing of the mylyn website (see http://wiki.eclipse.org/Mylyn/Website)
#    "libapache2-mod-php5",
#    "php5",
    ]

  package { $requirements:
    ensure  => "installed",
    require => Exec["apt-get update"],
  }

  service { "apache2":
    ensure  => running,
    require => Package["apache2"],
  }
 
  exec { "Enable cgi module":
    command => "a2enmod cgi",
    require => Package["apache2"],
    notify  => Service["apache2"],
    creates => "/etc/apache2/mods-enabled/cgi.load",
  }

  file { "/usr/lib/cgi-bin/services":
    source  => "puppet:///modules/mylyn/services.cgi",
    mode => 755,
    require => Package[$requirements],
  }

  file { "/var/www/html/index.html":
    content => template('mylyn/index.html.erb'),
    mode => 644,
    require => Package[$requirements],
  }  

#  for local testing of the mylyn website (see http://wiki.eclipse.org/Mylyn/Website)
#  file { "/var/www/html/mylyn":
#    ensure  => directory, # so make this a directory
#    recurse => true, # enable recursive directory management
#    purge   => true, # purge all unmanaged junk
#    force   => true, # also purge subdirs and links etc.
#    source => "puppet:///modules/mylyn/mylyn",
#    require => Package[$requirements],
#  }

#   file { "/var/www/html/eclipse.org-common":
#    ensure  => directory, # so make this a directory
#    recurse => true, # enable recursive directory management
#    purge   => true, # purge all unmanaged junk
#    force   => true, # also purge subdirs and links etc.
#    source => "puppet:///modules/mylyn/eclipse.org-common",
#    require => Package[$requirements],
#  }

}