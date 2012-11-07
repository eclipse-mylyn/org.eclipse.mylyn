define bugzilla::defaultsites {
$requirements = [
	"apache2",
	"apache2.2-common",
	"libapache2-mod-auth-mysql",
	"libapache2-mod-fcgid",
	"libapache2-mod-php5",
	"mysql-server",
	"bzr",
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
]

	package { $requirements: 
		ensure => "installed" ,
 	   require => Exec["apt-get update"],
	}
	
	exec { "Enable php5 module":
		command => "a2enmod php5",
		require => Package["libapache2-mod-php5"],
		creates => "/etc/apache2/mods-enabled/php5.load",
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

	service { "mysql":
				ensure => "running",
				enable => "true",
				require => Package["mysql-server"],
	}

	exec { "phpmyadmin_Apache2":
		command => "echo '#phpmyadmin\nInclude /etc/phpmyadmin/apache.conf' >>/etc/apache2/apache2.conf",
		require => [Package["phpmyadmin"],Package["libapache2-mod-php5"]],
		unless  => 'cat /etc/apache2/apache2.conf | grep "#phpmyadmin"'
	}
	
	file { "/etc/apache2/sites-enabled/001-default-ssl":
		ensure => link,
		target => "/etc/apache2/sites-available/default-ssl",
	}

	exec { "create $bugzilla::toolsDir":
		command => "mkdir $bugzilla::toolsDir",
		creates => "$bugzilla::toolsDir",
		require => Exec['phpmyadmin_Apache2']
	}

	exec { "create $bugzilla::bugzillaBase":
		command => "mkdir $bugzilla::bugzillaBase",
		creates => "$bugzilla::bugzillaBase",
		require => Exec["create $bugzilla::toolsDir"]
	}

	exec { "create $bugzilla::installHelper":
		command => "mkdir $bugzilla::installHelper",
		creates => "$bugzilla::installHelper",
		require => Exec[ "create $bugzilla::bugzillaBase"]
	}

	exec { "create $bugzilla::confDir":
		command => "mkdir $bugzilla::confDir",
		creates => "$bugzilla::confDir",
		require => Exec[ "create $bugzilla::bugzillaBase"]
	}

	exec { "create $bugzilla::installLog":
		command => "mkdir $bugzilla::installLog",
		creates => "$bugzilla::installLog",
		require => Exec[ "create $bugzilla::bugzillaBase"]
	}

	exec { "mysql-create-user-${bugzilla::dbuser}" :
		unless    => "/usr/bin/mysql --user='${bugzilla::dbuser}' --password='${bugzilla::dbuserPassword}'",
		command   => "/usr/bin/mysql -v --user='root' -e \"CREATE USER '${bugzilla::dbuser}'@localhost IDENTIFIED BY '${bugzilla::dbuserPassword}'\"",
		logoutput => true,
		require   => [ Package["mysql-server"],
		Exec["create $bugzilla::installHelper"],
		Exec["create $bugzilla::installLog"],
		Package[$requirements]	
		]
	}

	file { "/var/www/index.php":
		notify => Service["apache2"],
		ensure => "present",
		source => "puppet:///modules/bugzilla/index.php",
		owner => "root",
		group => "root",
		mode => 644,
		require => [Package["apache2"], Package["php5"]],	
	}

}