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

  /* can't use cwd => $envbase since that may not yet exist and would cause a cyclic dependency */
  exec { "stop $envid":
    command => "/bin/sh -c '(cd $envbase && $envbase/bin/gerrit.sh stop)'",
    require => Gerrit["$version"],
    user    => "$gerrit::userOwner",
    onlyif  => "test -x $envbase/bin/gerrit.sh && $envbase/bin/gerrit.sh check | grep -q 'Gerrit running'",
  }

  exec { "clear $envid":
    command => "rm -rf $envbase",
    require => Exec["stop $envid"],
    user    => "$gerrit::userOwner",
    onlyif  => "test -e $base/clearexisting",
  }

  exec { "configure $envid":
    command => "/usr/lib/jvm/java-7-openjdk-i386/jre/bin/java -jar $base/archive/gerrit-$version.war init --batch --site-path $envbase --no-auto-start",
    require => Exec["clear $envid"],
    user    => "$gerrit::userOwner",
    creates => "$envbase",
  }

  file { "$envbase/etc/gerrit.config":
    content => template('gerrit/gerrit.config.erb'),
    require => Exec["configure $envid"],
  }

  file { "$envbase":
    ensure  => "directory",
    owner   => "$gerrit::userOwner",
    group   => "$gerrit::userGroup",
    require => Exec["configure $envid"]
  }

  file { "$conf/$envid.conf":
    content => template('gerrit/gerrit.conf.erb'),
    owner   => "$gerrit::userOwner",
    group   => "$gerrit::userGroup",
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
  }

  file { "$envbase/tests.id_rsa":
    source  => "puppet:///modules/gerrit/tests.id_rsa",
    owner   => "$gerrit::userOwner",
    group   => "$gerrit::userGroup",
    require => File["$envbase"],
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

  exec { "start $envid":
    command => "$envbase/bin/gerrit.sh start",
    cwd     => "$envbase",
    user    => "$gerrit::userOwner",
    require => [Gerrit::User["admin user for $envid"], Gerrit::User["tests user for $envid"], File["$envbase/etc/gerrit.config"]],
    creates => "$envbase/log/gerrit.pid",
  }

  $ssh = "ssh -p $sshport -i $envbase/admin.id_rsa -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no admin@localhost"

  exec { "create project for $envid":
    command => "$ssh gerrit create-project --name org.eclipse.mylyn.test --empty-commit",
    #    user => "$gerrit::userOwner",
    require => [Exec["start $envid"], File["$envbase/admin.id_rsa"]],
    creates => "$envbase/git/org.eclipse.mylyn.test.git"
  }

  exec { "add $envbase to apache":
    command => "echo 'Include $base/conf.d/[^.#]*\n' >> /etc/apache2/conf.d/gerrit.conf",
    require => File["$conf/$envid.conf"],
    notify  => Service["apache2"],
    onlyif => "grep -qe '^Include $base/conf.d' /etc/apache2/conf.d/gerrit.conf; test $? != 0"
  }
  
}
