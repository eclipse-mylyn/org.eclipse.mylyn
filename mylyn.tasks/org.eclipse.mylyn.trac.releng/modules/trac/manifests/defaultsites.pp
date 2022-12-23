define trac::defaultsites ($base = $trac::base, $userOwner = $trac::userOwner, $userGroup = $trac::userGroup,) {
  include "trac"

  /* Defaults */

  Trac::Trac {
    base      => $base,
    userOwner => $userOwner,
    userGroup => $userGroup,
  }

  Trac::Plugin {
    base      => $base,
    userOwner => $userOwner,
    userGroup => $userGroup,
  }

  Trac::Site {
    base      => $base,
    version   => "1.0",
    require   => Trac["1.0"],
    userOwner => $userOwner,
    userGroup => $userGroup,
  }

  /* Instances */

  trac::trac { "1.0":
  }

  trac::trac { "1.0.1":
  }

  trac::trac { "trunk":
  }

  /* Plugins */

  trac::plugin { "accountmanagerplugin-0.11":
    url => "http://trac-hacks.org/svn/accountmanagerplugin/0.11",
    egg => "TracAccountManager",
  }

  trac::plugin { "masterticketsplugin-trunk":
    url => "http://trac-hacks.org/svn/masterticketsplugin/trunk",
    egg => "TracMasterTickets",
  }

  trac::plugin { "xmlrpcplugin-trunk":
    url => "http://trac-hacks.org/svn/xmlrpcplugin/trunk",
    egg => "TracXMLRPC",
  }

  /* Sites */

/* Disabling all Sites per bug 448427

  trac::site { "trac-1.0":
    version => "1.0",
    require => Trac["1.0"]
  }

  trac::site { "trac-1.0.1":
    version => "1.0.1",
    require => Trac["1.0.1"],
    envdefault => true,
  }

  trac::site { "trac-1.0-allbasic":
    allbasicauth => true,
    envinfo      => "AllBasicAuth",
  }

  trac::site { "trac-1.0-cert":
    certauth => true,
    envinfo  => "CertAuth",
  }

  trac::site { "trac-1.0-digest":
    digestauth => true,
    envinfo    => "DigestAuth",
  }

  trac::site { "trac-1.0-form-auth":
    accountmanagerplugin => "0.11",
    envinfo              => "FormAuth",
  }

  trac::site { "trac-trunk":
    version => "trunk",
    require => Trac["trunk"],
  }

  trac::site { "trac-test":
    envinfo    => "Test",
  }
*/

}