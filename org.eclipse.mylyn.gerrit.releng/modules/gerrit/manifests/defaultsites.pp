define gerrit::defaultsites ($base = $gerrit::base, $userOwner = $gerrit::userOwner, $userGroup = $gerrit::userGroup,) {
  include "gerrit"
  
  /* Defaults */

  Gerrit {
    base      => $base,
    userOwner => $userOwner,
    userGroup => $userGroup,
  }

  Gerrit::Site {
    base      => $base,
    userOwner => $userOwner,
    userGroup => $userGroup,
  }

  /* Instances */

  gerrit { "2.4.2":
  }

  gerrit { "2.5.2":
    postfix => "-full",
  }

  gerrit { "2.6-rc2":
  }

  gerrit { "2.7-rc0":
  }

  /* Sites */

  gerrit::site { "gerrit-2.4.2":
    version => "2.4.2",
    port    => 28242,
    sshport => 29242,
    envdefault => true,
    require => Gerrit["2.4.2"],
  }

  gerrit::site { "gerrit-2.5.2":
    version => "2.5.2",
    port    => 28252,
    sshport => 29252,
    require => Gerrit["2.5.2"],
  }

  gerrit::site { "gerrit-2.6-rc2":
    version => "2.6-rc2",
    port    => 28260,
    sshport => 29260,
    envinfo => "Test",
    require => Gerrit["2.6-rc2"],
  }

  gerrit::site { "gerrit-2.7-rc0":
    version => "2.7-rc0",
    port    => 28270,
    sshport => 29270,
    envinfo => "Test",
    require => Gerrit["2.7-rc0"],
  }


}