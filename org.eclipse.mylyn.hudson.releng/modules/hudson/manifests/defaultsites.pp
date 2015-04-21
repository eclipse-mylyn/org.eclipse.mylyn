define hudson::defaultsites ($base = $hudson::base,) {
  /* Defaults */

  Hudson::Hudson {
    base => "$base",
  }

  Hudson::Site {
    data => "hudson-2.1",
    base => "$base",
  }

  /* Instances */


  hudson::hudson { "3.1.2":
    type      => "hudson",
    qualifier => "eclipse",
  }

  hudson::hudson { "3.2.2":
    type      => "hudson",
    qualifier => "eclipse",
  }

  hudson::hudson { "1.565.3":
    type      => "jenkins",
    qualifier => "stable",
  }

  hudson::hudson { "1.596.2":
    type      => "jenkins",
    qualifier => "stable",
  }

  /* Sites */


  hudson::site { "hudson-3.1.2":
    envtype => "hudson",
    version => "3.1.2",
    port    => 9042,
    require => Hudson["3.1.2"],
  }

  hudson::site { "hudson-3.2.2":
    envtype => "hudson",
    version => "3.2.2",
    port    => 9322,
    envdefault => true,
    require => Hudson["3.2.2"],
  }

  hudson::site { "jenkins-1.565.3":
    envtype => "jenkins",
    version => "1.565.3",
    port    => 9565,
    require => Hudson["1.565.3"],
  }

  hudson::site { "jenkins-1.596.2":
    envtype => "jenkins",
    version => "1.596.2",
    port    => 9596,
    envdefault => true,
    require => Hudson["1.596.2"],
  }

}