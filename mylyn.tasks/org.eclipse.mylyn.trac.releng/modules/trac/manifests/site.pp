
define trac::site (
  $envid                = "$title",
  $version,
  $xmlrpcplugin         = "trunk",
  $accountmanagerplugin = "",
  $allbasicauth         = false,
  $certauth             = false,
  $digestauth           = false,
  $base                 = $trac::base,
  $envtype              = "trac",
  $envinfo              = "",
  $envdefault           = false,
  $userOwner            = $trac::userOwner,
  $userGroup            = $trac::userGroup,) {
  $prefix = "$base/share/trac-$version"
  $envbase = "$base/var/$envid"
  $env = "$base/var/$envid/env"
  $conf = "$base/conf.d"

  include "trac"

  exec { "prepare $envbase":
    command => "mkdir -p $base/bin $base/conf.d $base/src $base/var $envbase",
    creates => "$envbase",
    require => Exec["prepare trac"],
    user    => "$userOwner",
  }

  file { "$envbase":
    ensure  => "directory",
    owner   => "www-data",
    group   => "$userGroup",
    require => Exec["prepare $envbase"],
  }

  file { "$envbase/svn":
    ensure  => "directory",
    owner   => "www-data",
    group   => "$userGroup",
    require => File["$envbase"],
  }

  exec { "svn create $envid":
    command => "/usr/bin/svnadmin create $envbase/svn",
    require => File["$envbase/svn"],
    creates => "$envbase/svn/format",
  }

  exec { "initenv $envid":
    command => "$base/bin/tracadmin-$version $env initenv $envid sqlite:db/trac.db svn $envbase/svn",
    creates => "$env",
    user    => "www-data",
    require => Exec["svn create $envid"],
  }

  exec { "add admin permissions $envid":
    command     => "$base/bin/tracadmin-$version $env permission add admin@mylyn.eclipse.org TRAC_ADMIN",
    user        => "www-data",
    environment => "PYTHON_EGG_CACHE=/tmp/eggs",
    require     => Exec["initenv $envid"],
    onlyif      => 
    "$base/bin/tracadmin-$version $env permission list admin@mylyn.eclipse.org | (grep -qE 'admin.*TRAC_ADMIN'; test $? != 0)"
  }

  exec { "add tests permissions $envid":
    command     => 
    "$base/bin/tracadmin-$version $env permission add tests@mylyn.eclipse.org TICKET_ADMIN TICKET_CREATE TICKET_MODIFY",
    user        => "www-data",
    environment => "PYTHON_EGG_CACHE=/tmp/eggs",
    require     => Exec["initenv $envid"],
    onlyif      => 
    "$base/bin/tracadmin-$version $env permission list tests@mylyn.eclipse.org | (grep -qE 'tests.*TICKET_ADMIN'; test $? != 0)"
  }

  exec { "add user permissions $envid":
    command     => "$base/bin/tracadmin-$version $env permission add user@mylyn.eclipse.org TICKET_CREATE TICKET_MODIFY",
    user        => "www-data",
    environment => "PYTHON_EGG_CACHE=/tmp/eggs",
    require     => Exec["initenv $envid"],
    onlyif      => 
    "$base/bin/tracadmin-$version $env permission list user@mylyn.eclipse.org | (grep -qE 'user.*TICKET_CREATE'; test $? != 0)"
  }

  file { "$env/conf/trac.ini":
    content => template('trac/trac.ini.erb'),
    require => Exec["initenv $envid"],
    owner   => "www-data",
    group   => "$userGroup",
  }

  file { "$conf/$envid.conf":
    content => template('trac/trac.conf.erb'),
    require => Exec["prepare $envbase"],
    owner   => "$userOwner",
    group   => "$userGroup",
  }

  if $digestauth {
    file { "$envbase/htpasswd.digest":
      content => template('trac/htpasswd.digest.erb'),
      require => File["$envbase"],
      owner   => "$userOwner",
      group   => "$userGroup",
    }
  } else {
    file { "$envbase/htpasswd":
      content => template('trac/htpasswd.erb'),
      require => File["$envbase"],
      owner   => "$userOwner",
      group   => "$userGroup",
    }
  }

  file { "$envbase/trac-$version.fcgi":
    content => template('trac/trac.fcgi.erb'),
    mode    => 755,
    require => File["$envbase"],
    owner   => "$userOwner",
    group   => "$userGroup",
  }

  if $xmlrpcplugin {
    file { "$env/plugins/TracXMLRPC.egg":
      source  => "$base/src/xmlrpcplugin-$xmlrpcplugin/src/dist/TracXMLRPC.egg",
      require => Exec["initenv $envid"],
      owner   => "$userOwner",
      group   => "$userGroup",
    }

    exec { "add xmlrpc permissions $envid":
      command     => "$base/bin/tracadmin-$version $env permission add tests@mylyn.eclipse.org XML_RPC",
      user        => "www-data",
      environment => "PYTHON_EGG_CACHE=/tmp/eggs",
      require     => File["$env/plugins/TracXMLRPC.egg"],
      onlyif      => 
      "$base/bin/tracadmin-$version $env permission list tests@mylyn.eclipse.org | (grep -qE 'tests.*XML_RPC'; test $? != 0)"
    }
  }

  if $accountmanagerplugin {
    file { "$env/plugins/TracAccountManager.egg":
      source  => "$base/src/accountmanagerplugin-$accountmanagerplugin/src/dist/TracAccountManager.egg",
      require => Exec["initenv $envid"],
      owner   => "$userOwner",
      group   => "$userGroup",
    }
  }

  exec { "add $envbase to /etc/apache2/conf.d/trac.conf":
    command => "echo 'Include $base/conf.d/[^.#]*\n' >> /etc/apache2/conf.d/trac.conf",
    require => File["$conf/$envid.conf"],
    notify  => Service["apache2"],
    onlyif  => "grep -qe '^Include $base/conf.d' /etc/apache2/conf.d/trac.conf; test $? != 0"
  }

  trac::service { "${envid}-xml-rpc":
    envid      => "$title",
    version    => "$version",
    envinfo    => "$envinfo",
    envdefault => $envdefault,
    envmode    => "XML-RPC",
    accessmode => "XML_RPC",
  }

  trac::service { "${envid}-web":
    envid      => "$title",
    version    => "$version",
    envinfo    => "$envinfo",
    envdefault => false,
    envmode    => "Web",
    accessmode => "TRAC_0_9",
  }

}
