define trac::defaultsites {

/* Trac automatically provisions during install "python-genshi" */

$requirements = [ "python-pysqlite2", "python-setuptools", "python-subversion", "subversion", ]
package { $requirements: 
	ensure => "installed" 
}

/* Defaults. */
Trac {
	require => Package[$requirements],
}

Trac::Site {
	version => "1.0",
	require => Trac["1.0"],	
}

trac { "0.11.7":
}

trac { "0.12.4":
}

trac { "1.0":
}

trac { "trunk":
}

trac::plugin { "accountmanagerplugin-0.11":
	url => "http://trac-hacks.org/svn/accountmanagerplugin/0.11",
	egg => "TracAccountManager",
}

trac::plugin { "masterticketsplugin-0.11":
	url => "http://trac-hacks.org/svn/masterticketsplugin/0.11",
	egg => "TracMasterTickets",
}

trac::plugin { "xmlrpcplugin-trunk":
	url => "http://trac-hacks.org/svn/xmlrpcplugin/trunk",
	egg => "TracXMLRPC",
}

trac::site { "trac-0.11":
	version => "0.11.7",
	require => Trac["0.11.7"],
}

trac::site { "trac-0.12":
	version => "0.12.4",
	require => Trac["0.12.4"],
}

trac::site { "trac-1.0":
	version => "1.0",
	require => Trac["1.0"],
}

trac::site { "trac-allbasic":
	allbasicauth => true,
}

trac::site { "trac-cert":
	certauth => true,
}

trac::site { "trac-digest":
	digestauth => true,
}

trac::site { "trac-form-auth":
	accountmanagerplugin => "0.11"
}

trac::site { "trac-trunk":
	version => "trunk",
	require => Trac["trunk"],
}

trac::site { "trac-test":
}

}