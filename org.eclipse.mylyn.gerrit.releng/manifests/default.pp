Exec { path => [ "/bin/", "/sbin/" , "/usr/bin/", "/usr/sbin/" ] }

$tools = "/tmp/tools"

include apache

class apache {
  
	package { "apache2":
		ensure => present,
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
		require => Exec["Enable ssl module"],
	}

}

file { "/etc/apache2/conf.d/gerrit.conf":
  content => "Include $tools/gerrit/conf.d/[^.#]*\n",
  require => [ Package["apache2"], Gerrit::Defaultsites["gerrit"], ],
  notify  => Service["apache2"],
}

Gerrit {
	base => "$tools/gerrit",
}
Gerrit::Site {
	base => "$tools/gerrit",
}

gerrit::defaultsites { "gerrit":
}

