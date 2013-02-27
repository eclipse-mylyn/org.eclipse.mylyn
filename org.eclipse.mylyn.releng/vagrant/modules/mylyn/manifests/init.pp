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
class mylyn {

  exec { "apt-get update":
    command => "apt-get update",
    onlyif  => "find /var/lib/apt/lists/ -mtime -7 | (grep -q Package; [ $? != 0 ])",
  }
  
  $requirements = [
    "apache2",
    "apache2.2-common",
    "libfile-find-rule-perl-perl"
    ]

  package { $requirements:
    ensure  => "installed",
    require => Exec["apt-get update"],
  }

  service { "apache2":
    ensure  => running,
    require => Package["apache2"],
  }
 
  file { "/usr/lib/cgi-bin/services":
    source  => "puppet:///modules/mylyn/services.cgi",
    mode => 755,
    require => Package[$requirements],
  }

  file { "/var/www/index.html":
    content => template('mylyn/index.html.erb'),
    mode => 644,
    require => Package[$requirements],
  }

}
 