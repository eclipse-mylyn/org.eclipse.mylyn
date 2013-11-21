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

  gerrit { "2.8-rc2":
  }

  /* Sites */

  gerrit::site { "gerrit-2.6.1":
    version => "2.6.1",
    port    => 28261,
    sshport => 29261,
    require => Gerrit["2.6.1"],
  }

  gerrit::site { "gerrit-2.7-dev":
    version => "2.7",
    port    => 26270,
    sshport => 27270,
    authtype => "DEVELOPMENT_BECOME_ANY_ACCOUNT",
    require => Gerrit["2.7"],
  }

  gerrit::site { "gerrit-2.7":
    version => "2.7",
    port    => 28270,
    sshport => 29270,
    envdefault => true,
    require => Gerrit["2.7"],
  }

  gerrit::site { "gerrit-2.8-rc2":
    version => "2.8-rc2",
    port    => 28280,
    sshport => 29280,
    envinfo => "Test",
    require => Gerrit["2.8-rc2"],
  }

}