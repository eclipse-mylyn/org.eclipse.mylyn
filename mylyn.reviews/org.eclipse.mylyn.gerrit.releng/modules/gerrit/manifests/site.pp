define gerrit::site (
  $envid        = "$title",
  $port,
  $sshport,
  $version,
  $allbasicauth = false,
  $certauth     = false,
  $digestauth   = false,
  $authtype     = "HTTP",
  $envtype      = "gerrit",
  $envinfo      = "",
  $envdefault = false,
  $base         = $gerrit::base,
  $userOwner    = $gerrit::userOwner,
  $userGroup    = $gerrit::userGroup,) {
  $envbase = "$base/$envid"
  $conf = "$base/conf.d"
  $envhost = regsubst(file("/etc/hostname"), '\n', '')
  $versionArray = split($version, '[.]')
  $Setup213  = (($versionArray[0] == "2") and ($versionArray[1] >= "13")) or ($versionArray[0] > "2")

  /* can't use cwd => $envbase since that may not yet exist and would cause a cyclic dependency */
  exec { "stop $envid":
    command => "/bin/sh -c '(cd $envbase && $envbase/bin/gerrit.sh stop)'",
    require => Gerrit["$version"],
    user    => "$gerrit::userOwner",
    onlyif  => "test -x $envbase/bin/gerrit.sh && $envbase/bin/gerrit.sh check | grep -q 'Gerrit running'",
  }
 
  $gerritUserPassword = 'mylyntest'

  exec { "clear $envid":
    command => "rm -rf $envbase",
    require => Exec["stop $envid"],
    user    => "$gerrit::userOwner",
    onlyif  => "grep -qe '^clear' /etc/gerrit_clear_mode",
  }

   file { "$envbase":
    ensure  => "directory",
    owner   => "$gerrit::userOwner",
    group   => "$gerrit::userGroup",
    require => Exec["clear $envid"]
  }

  file { "$envbase/etc":
    ensure  => "directory",
    owner   => "$gerrit::userOwner",
    group   => "$gerrit::userGroup",
    require => [Exec["clear $envid"],File["$envbase"]],
  }

  file { "$envbase/etc/gerrit.config":
    content => template('gerrit/gerrit.config.erb'),
    owner   => "$gerrit::userOwner",
    group   => "$gerrit::userGroup",
    require => File["$envbase/etc"],
  }

  file { "$conf/$envid.conf":
    content => template('gerrit/gerrit.conf.erb'),
    owner   => "$gerrit::userOwner",
    group   => "$gerrit::userGroup",
    require => [Exec["clear $envid"],File["$envbase"]],
    notify  => Service["apache2"],
  }

  if $digestauth {
    file { "$envbase/htpasswd.digest":
      content => template('gerrit/htpasswd.digest.erb'),
      owner   => "$gerrit::userOwner",
      group   => "$gerrit::userGroup",
      require => File["$envbase"],
    }
  } else {
    file { "$envbase/htpasswd":
      content => template('gerrit/htpasswd.erb'),
      owner   => "$gerrit::userOwner",
      group   => "$gerrit::userGroup",
      require => File["$envbase"],
    }
  }

  file { "$envbase/service.json":
    content => template('gerrit/service.json.erb'),
    owner   => "$gerrit::userOwner",
    group   => "$gerrit::userGroup",
    require => File["$envbase"],
  }

  file { "$envbase/admin.id_rsa":
    source  => "puppet:///modules/gerrit/admin.id_rsa",
    owner   => "$gerrit::userOwner",
    group   => "$gerrit::userGroup",
    require => File["$envbase"],
    mode    => 0600,
  }

  file { "$envbase/tests.id_rsa":
    source  => "puppet:///modules/gerrit/tests.id_rsa",
    owner   => "$gerrit::userOwner",
    group   => "$gerrit::userGroup",
    require => File["$envbase"],
    mode    => 0600,
  }

  exec { "configure $envid":
    command => "/usr/bin/java -jar $base/archive/gerrit-$version.war init --batch --site-path $envbase --no-auto-start",
    require => [Exec["clear $envid"],File["$envbase/etc/gerrit.config"]],
    user    => "$gerrit::userOwner",
    creates => "$envbase/git",
  }

  gerrit::user { "admin user for $envid":
    username  => "admin",
    userid    => 1000000,
    useremail => "admin@mylyn.eclipse.org",
    userkey   => template('gerrit/admin.id_rsa.pub'),
    usergroup => "Administrators",
    base      => $base,
    envid     => $envid,
    require   => [Exec["configure $envid"],],
  }

  gerrit::user { "tests user for $envid":
    username  => "tests",
    userid    => 1000001,
    useremail => "tests@mylyn.eclipse.org",
    userkey   => template('gerrit/tests.id_rsa.pub'),
    usergroup => "N/A",
    base      => $base,
    envid     => $envid,
    require   => [Exec["configure $envid"],]
  }

  exec { "reindex $envid":
    command => "/usr/bin/java -jar $base/archive/gerrit-$version.war reindex --site-path $envbase",
    user    => "$gerrit::userOwner",
    require => [Exec["configure $envid"], Gerrit::User["admin user for $envid"], Gerrit::User["tests user for $envid"], File["$envbase/etc/gerrit.config"]],
  }

  if $Setup213 {
    file { "$envbase/git/All-Users.git.tar":
      source  => "puppet:///modules/gerrit/All-Users.git.tar",
      owner   => "$gerrit::userOwner",
      group   => "$gerrit::userGroup",
      require => [Exec["reindex $envid"], Gerrit::User["admin user for $envid"], Gerrit::User["tests user for $envid"], File["$envbase/etc/gerrit.config"]],
    }

    exec { "del $envbase/git/All-Users.git":
      command => "rm -R $envbase/git/All-Users.git",
      cwd     => "$envbase/git",
      user    => "$gerrit::userOwner",
      require => File["$envbase/git/All-Users.git.tar"],
      onlyif  => "test -e $envbase/git/All-Users.git",
      logoutput => true,
    }

    exec { "untar $envbase/git/All-Users.git.tar":
      command => "tar zxvf $envbase/git/All-Users.git.tar",
      cwd     => "$envbase/git",
      user    => "$gerrit::userOwner",
      require => Exec["del $envbase/git/All-Users.git"],
      notify  => Exec["end prepare setup $envbase"],
      creates => "$envbase/git/All-Users.git",
      logoutput => true,
    }
  } else {
    exec { "dummy $envbase/git/All-Users.git.tar":
      command => "echo $envbase",
      require => [Exec["reindex $envid"], Gerrit::User["admin user for $envid"], Gerrit::User["tests user for $envid"], File["$envbase/etc/gerrit.config"]],
      notify  => Exec["end prepare setup $envbase"],
      logoutput => true,
    }
  }

  exec { "end prepare setup $envbase":
      command => "echo 'end prepare setup $envbase'",
      logoutput => true,
    }
  exec { "start $envid":
    command => "$envbase/bin/gerrit.sh start",
    cwd     => "$envbase",
    user    => "$gerrit::userOwner",
    creates => "$envbase/log/gerrit.pid",
    require => Exec["end prepare setup $envbase"],
    logoutput => true,
  }

  $ssh = "ssh -p $sshport -i $envbase/admin.id_rsa -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no admin@localhost"

  exec { "create project for $envid":
    command => "$ssh gerrit create-project --empty-commit org.eclipse.mylyn.test",
    user    => "root",
    require => [Exec["start $envid"], File["$envbase/admin.id_rsa"]],
    creates => "$envbase/git/org.eclipse.mylyn.test.git"
  }

  exec { "add $envbase to apache":
    command => "echo 'Include $base/conf.d/[^.#]*\n' >> /etc/apache2/conf-enabled/gerrit.conf",
    require => [File["$conf/$envid.conf"], Package["apache2"], ],
    notify  => Service["apache2"],
    onlyif => "grep -qe '^Include $base/conf.d' /etc/apache2/conf-enabled/gerrit.conf; test $? != 0"
  }
}