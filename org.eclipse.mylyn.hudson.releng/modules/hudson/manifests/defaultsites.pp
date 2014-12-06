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

  hudson::hudson { "3.2.1":
    type      => "hudson",
    qualifier => "eclipse",
  }

  hudson::hudson { "1.565.3":
    type      => "jenkins",
    qualifier => "stable",
  }

  hudson::hudson { "1.580.1":
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

  hudson::site { "hudson-3.2.1":
    envtype => "hudson",
    version => "3.2.1",
    port    => 9321,
    envdefault => true,
    require => Hudson["3.2.1"],
  }

  hudson::site { "jenkins-1.565.3":
    envtype => "jenkins",
    version => "1.565.3",
    port    => 9565,
    require => Hudson["1.565.3"],
  }

  hudson::site { "jenkins-1.580.1":
    envtype => "jenkins",
    version => "1.580.1",
    port    => 9580,
    envdefault => true,
    require => Hudson["1.580.1"],
  }

}