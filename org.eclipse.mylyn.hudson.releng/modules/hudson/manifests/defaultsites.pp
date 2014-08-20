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
  
  hudson::hudson { "3.2.0":
    type      => "hudson",
    qualifier => "eclipse",
  }

  hudson::hudson { "1.554.3":
    type      => "jenkins",
    qualifier => "stable",
  }

  hudson::hudson { "1.565.1":
    type      => "jenkins",
    qualifier => "stable",
  }

  hudson::hudson { "jenkins-latest":
    type      => "jenkins",
    qualifier => "latest",
  }

  /* Sites */


  hudson::site { "hudson-3.1.2":
    envtype => "hudson",
    version => "3.1.2",
    port    => 9042,
    require => Hudson["3.1.2"],
  }

  hudson::site { "hudson-3.2.0":
    envtype => "hudson",
    version => "3.2.0",
    port    => 9032,
    envdefault => true,
    require => Hudson["3.2.0"],
  }

  hudson::site { "jenkins-1.554.3":
    envtype => "jenkins",
    version => "1.554.3",
    port    => 9134,
    require => Hudson["1.554.3"],
  }

  hudson::site { "jenkins-1.565.1":
    envtype => "jenkins",
    version => "1.565.1",
    port    => 9565,
    envdefault => true,
    require => Hudson["1.565.1"],
  }

  hudson::site { "jenkins-latest":
    envtype => "jenkins",
    version => "jenkins-latest",
    port    => 9110,
    require => Hudson["jenkins-latest"],
  }

}