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

  hudson::hudson { "2.1.0":
    type => "hudson",
  }

  hudson::hudson { "2.2.1":
    type => "hudson",
  }

  hudson::hudson { "3.0.0-bundled":
    type       => "hudson",
    qualifier  => "bundled",
  }

  hudson::hudson { "3.0.1":
    type      => "hudson",
    qualifier => "eclipse",
  }

  hudson::hudson { "1.466.2":
    type      => "jenkins",
    qualifier => "stable",
  }

  hudson::hudson { "1.480.3":
    type      => "jenkins",
    qualifier => "stable",
  }

  hudson::hudson { "jenkins-latest":
    type      => "jenkins",
    qualifier => "latest",
  }

  /* Sites */

  hudson::site { "hudson-2.1.0":
    envtype => "hudson",
    version => "2.1.0",
    port    => 9010,
    require => Hudson["2.1.0"],
  }

  hudson::site { "hudson-2.2.1":
    envtype => "hudson",
    version => "2.2.1",
    port    => 9020,
    require => Hudson["2.2.1"],
  }

  hudson::site { "hudson-3.0.0":
    envtype => "hudson",
    version => "3.0.0-bundled",
    port    => 9030,
    envdefault => true,
    require => Hudson["3.0.0-bundled"],
  }

  hudson::site { "hudson-3.0.1":
    envtype => "hudson",
    version => "3.0.1",
    port    => 9031,
    require => Hudson["3.0.1"],
  }

  hudson::site { "jenkins-1.466.2":
    envtype => "jenkins",
    version => "1.466.2",
    port    => 9112,
    require => Hudson["1.466.2"],
  }

  hudson::site { "jenkins-1.480.3":
    envtype => "jenkins",
    version => "1.480.3",
    port    => 9123,
    require => Hudson["1.480.3"],
  }

  hudson::site { "jenkins-latest":
    envtype => "jenkins",
    version => "jenkins-latest",
    port    => 9110,
    require => Hudson["jenkins-latest"],
  }

}