define bugzillaVersion (
  $major,
  $minor,
  $branch               = " ",
  $bugz_dbname          = "$title",

  $www_url              = "$title",
  $version              = "$title",
  $branchTag            = "bugzilla-stable",
  $custom_wf            = false,
  $custom_wf_and_status = false,
  $xmlrpc_enabled       = true,
  $base                 = "/home/tools/bugzilla") {
    
  if $major == "3" {
    if $minor == "6" {
      $VersionCreateName = "name"
    } else {
      $VersionCreateName = "value"
    }
  } else {
    $VersionCreateName = "value"
  }
  
  if ($branch == "trunk") {
  	$branchName = "$branch"
  } else {
   	$branchName = "${major}.${minor}"
  }

  if $branchTag == "trunk" {
    exec { "extract bugzilla $version":
      command => "bzr co bzr://bzr.mozilla.org/bugzilla/$branchName $version",
      cwd     => "$base",
      user => "$bugzilla::userOwner",
      timeout => 300,
      creates => "$base/$version",
    }
  } else {
    exec { "extract bugzilla $version":
      command => "bzr co -r tag:$branchTag bzr://bzr.mozilla.org/bugzilla/$branchName $version",
      cwd     => "$base",
      user => "$bugzilla::userOwner",
      timeout => 300,
      creates => "$base/$version",
    }
  }

  exec { "mysql-grant-${bugz_dbname}-${bugzilla::dbuser}":
    unless    => 
    "/usr/bin/mysql --user=root --batch -e \"SELECT user FROM db WHERE Host='localhost' and Db='${bugz_dbname}' and User='${bugzilla::dbuser}'\" mysql | /bin/grep '${bugzilla::dbuser}'",
    command   => "/usr/bin/mysql --verbose --user=root -e \"GRANT ALL ON ${bugz_dbname}.* TO '${bugzilla::dbuser}'@localhost\" \
        		; /usr/bin/mysqladmin --verbose --user=root flush-privileges",
    logoutput => true,
    require   => Exec["extract bugzilla $version"]
  }

  exec { "mysql-dropdb-$version":
    onlyif    => "/usr/bin/mysql --user=root '${bugz_dbname}'",
    command   => "/usr/bin/mysqladmin -v --user=root --force drop '${bugz_dbname}'",
    logoutput => true,
    require   => Exec["mysql-grant-${bugz_dbname}-${bugzilla::dbuser}"]
  }

  exec { "mysql-createdb-$version":
    unless    => "/usr/bin/mysql --user=root '${bugz_dbname}'",
    command   => "/usr/bin/mysqladmin -v --user=root --force create '${bugz_dbname}'",
    logoutput => true,
    require   => Exec["mysql-dropdb-$version"]
  }

  file { "$bugzilla::installHelper/answers$version":
    content => template('bugzilla/answers.erb'),
    owner   => "$bugzilla::userOwner",
    group   => "$bugzilla::userGroup",
    require => Exec["extract bugzilla $version"],
  }

  file { "$base/$version/extensions/Mylyn":
    ensure  => directory, # so make this a directory
    recurse => true, # enable recursive directory management
    purge   => true, # purge all unmanaged junk
    force   => true, # also purge subdirs and links etc.
    owner   => "$bugzilla::userOwner",
    group   => "$bugzilla::userGroup",
    source  => "puppet:///modules/bugzilla/extensions/Mylyn",
    require => Exec["extract bugzilla $version"],
  }

  file { "$base/$version/extensions/Mylyn/Extension.pm":
    content => template('bugzilla/Extension.pm.erb'),
    require => FILE["$base/$version/extensions/Mylyn"],
    owner   => "$bugzilla::userOwner",
    group   => "$bugzilla::userGroup",
    mode    => 0644,
  }

  exec { "init bugzilla_checksetup $version":
    command => "$base/$version/checksetup.pl $bugzilla::installHelper/answers$version -verbose",
    cwd     => "$base/$version",
    creates => "$base/$version/localconfig",
    user => "$bugzilla::userOwner",
    require => [
      EXEC["mysql-createdb-$version"],
      File["$bugzilla::installHelper/answers$version"],
      FILE["$base/$version/extensions/Mylyn/Extension.pm"]]
  }

  exec { "update bugzilla_checksetup $version":
    command   => "$base/$version/checksetup.pl $bugzilla::installHelper/answers$version -verbose",
    cwd       => "$base/$version",
    user => "$bugzilla::userOwner",
    logoutput => true,
    require   => [
      EXEC["mysql-createdb-$version"],
      EXEC["init bugzilla_checksetup $version"],
      File["$bugzilla::installHelper/answers$version"],
      FILE["$base/$version/extensions/Mylyn/Extension.pm"]]
  }

  if !$xmlrpc_enabled {
    file { "$base/$version/xmlrpc.cgi":
      content => template('bugzilla/xmlrpc.cgi.erb'),
      owner   => "$bugzilla::userOwner",
      group   => "$bugzilla::userGroup",
      mode    => 755,
      require => Exec["update bugzilla_checksetup $version"],
    }
  }

  file { "$bugzilla::confDir/$version.conf":
    content => template('bugzilla/apache2.conf.erb'),
    require => [Package["apache2"], EXEC["update bugzilla_checksetup $version"]],
    notify  => Service["apache2"],
  }

}