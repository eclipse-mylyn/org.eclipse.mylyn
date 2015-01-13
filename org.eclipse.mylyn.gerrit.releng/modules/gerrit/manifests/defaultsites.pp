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

  gerrit { "2.6.1":
  }

  gerrit { "2.7":
  }

  gerrit { "2.8.5":
  }

  gerrit { "2.9.4":
  }

  /* Sites */

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

  gerrit::site { "gerrit-2.8.5":
    version => "2.8.5",
    port    => 28285,
    sshport => 29285,
    require => Gerrit["2.8.5"],
  }

  gerrit::site { "gerrit-2.9.4":
    version => "2.9.4",
    port    => 28294,
    sshport => 29294,
    envdefault => true,
    require => Gerrit["2.9.4"],
  }

}