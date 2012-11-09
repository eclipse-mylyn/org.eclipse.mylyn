define trac::defaultsites (
  $base = $trac::base,
) {

include "trac"

/* Defaults */

Trac::Trac {
  base => $base,
}
Trac::Plugin {
  base => $base,
}
Trac::Site {
  base => $base,
	version => "1.0",
	require => Trac["1.0"],	
}

/* Instances */

trac::trac { "0.11.7":
}

trac::trac { "0.12.4":
}

trac::trac { "1.0":
}

trac::trac { "trunk":
}

/* Plugins */

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

/* Sites */

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
	envinfo => "AllBasicAuth",
}

trac::site { "trac-cert":
	certauth => true,
	envinfo => "CertAuth",
}

trac::site { "trac-digest":
	digestauth => true,
	envinfo => "DigestAuth",
}

trac::site { "trac-form-auth":
	accountmanagerplugin => "0.11",
	envinfo => "FormAuth",
}

trac::site { "trac-trunk":
	version => "trunk",
	require => Trac["trunk"],
}

trac::site { "trac-test":
}

}