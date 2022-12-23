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

  gerrit { "2.11.10":
  }

  gerrit { "2.13.8":
  }
  
  /* Sites */

  gerrit::site { "gerrit-2.11.10":
    version => "2.11.10",
    port    => 28211,
    sshport => 29211,
    envdefault => true,
    require => Gerrit["2.11.10"],
  }

  gerrit::site { "gerrit-2.13.8":
    version => "2.13.8",
    port    => 28213,
    sshport => 29213,
    require => Gerrit["2.13.8"],
  }

}
