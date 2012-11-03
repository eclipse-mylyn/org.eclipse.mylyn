Exec { path => [ "/bin/", "/sbin/" , "/usr/bin/", "/usr/sbin/" ] }

$tools = "/tmp/tools"

include apache

class apache {
	package { "apache2":
		ensure => present,
	}
	
	package { "libapache2-mod-fcgid" :
	    ensure => "installed",
	}
	
	service { "apache2":
		ensure => running,
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
	
	file { "/etc/apache2/sites-enabled/001-default-ssl":
		ensure => link,
		target => "/etc/apache2/sites-available/default-ssl",
	}

}

exec { "apt-get update":
    command => "apt-get update",
    onlyif => "find /var/lib/apt/lists/ -mtime -7 | (grep -q Package; [ $? != 0 ])",
}

package { "openjdk-6-jre" :
    ensure => "installed",
    require => Exec["apt-get update"],
}

Trac {
	base => "$tools/trac",
}
Trac::Plugin {
	base => "$tools/trac",
}
Trac::Site {
	base => "$tools/trac",
}

trac::defaultsites { "trac":
}

file { "/etc/apache2/conf.d/trac.conf":
	content => "DefaultInitEnv PYTHON_EGG_CACHE /tmp/eggs\nInclude $tools/trac/conf.d/[^.#]*\n",
	require => Package["apache2"],
	notify  => Service["apache2"],
}