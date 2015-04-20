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

  gerrit { "2.9.4":
  }

  /* Sites */

  gerrit::site { "gerrit-2.9.4":
    version => "2.9.4",
    port    => 28294,
    sshport => 29294,
    envdefault => true,
    require => Gerrit["2.9.4"],
  }

}