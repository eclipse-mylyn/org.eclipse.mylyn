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

  gerrit { "2.4.4":
  }

  gerrit { "2.5.6":
  }

  gerrit { "2.6.1":
  }

  gerrit { "2.7":
  }

  /* Sites */

  gerrit::site { "gerrit-2.4.4":
    version => "2.4.4",
    port    => 28244,
    sshport => 29244,
    require => Gerrit["2.4.4"],
  }

  gerrit::site { "gerrit-2.5.6":
    version => "2.5.6",
    port    => 28256,
    sshport => 29256,
    envdefault => true,
    require => Gerrit["2.5.6"],
  }

  gerrit::site { "gerrit-2.5.6-dev":
    version => "2.5.6",
    port    => 26256,
    sshport => 27256,
    authtype => "DEVELOPMENT_BECOME_ANY_ACCOUNT",
    require => Gerrit["2.5.6"],
  }

  gerrit::site { "gerrit-2.6.1":
    version => "2.6.1",
    port    => 28261,
    sshport => 29261,
    require => Gerrit["2.6.1"],
  }

  gerrit::site { "gerrit-2.7":
    version => "2.7",
    port    => 28270,
    sshport => 29270,
    require => Gerrit["2.7"],
  }


}