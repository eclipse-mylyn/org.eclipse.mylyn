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

  hudson::hudson { "3.2.2":
    type      => "hudson",
    qualifier => "eclipse",
  }

  hudson::hudson { "3.3.0":
    type      => "hudson",
    qualifier => "eclipse",
  }

  hudson::hudson { "1.565.3":
    type      => "jenkins",
    qualifier => "stable",
  }

  hudson::hudson { "1.609.2":
    type      => "jenkins",
    qualifier => "stable",
  }

  /* Sites */


  hudson::site { "hudson-3.2.2":
    envtype => "hudson",
    version => "3.2.2",
    port    => 9322,
    require => Hudson["3.2.2"],
  }

  hudson::site { "hudson-3.3.0":
    envtype => "hudson",
    version => "3.3.0",
    port    => 9330,
    envdefault => true,
    require => Hudson["3.3.0"],
  }

  hudson::site { "jenkins-1.565.3":
    envtype => "jenkins",
    version => "1.565.3",
    port    => 9565,
    require => Hudson["1.565.3"],
    folderPlugin => true,
  }

  hudson::site { "jenkins-1.609.2":
    envtype => "jenkins",
    version => "1.609.2",
    port    => 9609,
    envdefault => true,
    require => Hudson["1.609.2"],
    folderPlugin => true,
  }

}