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
  
  gerrit { "2.10":
  }
  
  gerrit { "2.11.2":
  }
  
  /* Sites */

  gerrit::site { "gerrit-2.9.4":
    version => "2.9.4",
    port    => 28294,
    sshport => 29294,
    require => Gerrit["2.9.4"],
  }
  
  gerrit::site { "gerrit-2.10":
    version => "2.10",
    port    => 28210,
    sshport => 29210,
    require => Gerrit["2.10"],
  }
    
  gerrit::site { "gerrit-2.11.2":
    version => "2.11.2",
    port    => 28211,
    sshport => 29211,
    envdefault => true,
    require => Gerrit["2.11.2"],
  }

}
