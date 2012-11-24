define trac::trac ( #
  $version   = "$title",
  $base      = $trac::base,
  $userOwner = $trac::userOwner,
  $userGroup = $trac::userGroup,) {
  $binbase = "$base/bin"
  $srcbase = "$base/src/trac-$version"
  $prefix = "$base/share/trac-$version"

  include "trac"

  exec { "prepare $version":
    command => "mkdir -p $binbase $srcbase $prefix",
    creates => ["$binbase", "$srcbase", "$prefix"],
    require => Exec["prepare trac"],
    user    => "$userOwner",
  }

  file { "$srcbase":
    ensure  => "directory",
    require => Exec["prepare $version"],
    owner   => "$userOwner",
    group   => "$userGroup",
  }

  if $version == "trunk" {
    exec { "extract trac $version":
      command => "svn checkout http://svn.edgewall.org/repos/trac/trunk Trac-trunk",
      cwd     => "$srcbase",
      creates => "$srcbase/Trac-$version",
      require => File["$srcbase"],
      user    => "$userOwner",
    }
  } else {
    exec { "download trac $version":
      command => "wget -O $srcbase/Trac-$version.tar.gz http://download.edgewall.org/trac/Trac-$version.tar.gz",
      creates => "$srcbase/Trac-$version.tar.gz",
      require => File["$srcbase"],
      user    => "$userOwner",
    }

    exec { "extract trac $version":
      command => "tar -C $srcbase -xzvf $srcbase/Trac-$version.tar.gz",
      require => Exec["download trac $version"],
      creates => "$srcbase/Trac-$version",
      user    => "$userOwner",
    }
  }

  file { "$srcbase/install.sh":
    source => "puppet:///modules/trac/install.sh",
    mode   => 755,
    owner  => "$userOwner",
    group  => "$userGroup",
  }

  exec { "install $version":
    command   => "$srcbase/install.sh $srcbase/Trac-$version $prefix $version",
    path      => ".",
    logoutput => false,
    require   => Exec["extract trac $version"],
    creates   => "$prefix/lib/.provisioned",
    user      => "$userOwner",
  }

  file { "$binbase/trac-$version.cgi":
    content => template('trac/trac.cgi.erb'),
    require => Exec["prepare $version"],
    mode    => 755,
    owner   => "$userOwner",
    group   => "$userGroup",
  }

  file { "$binbase/tracadmin-$version":
    content => template('trac/tracadmin.erb'),
    mode    => 755,
    require => Exec["prepare $version"],
    owner   => "$userOwner",
    group   => "$userGroup",
  }

}