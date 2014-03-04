/*******************************************************************************
 * Copyright (c) 2012 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *     Steffen Pingel (Tasktop Techologies)
 *******************************************************************************/
define bugzilla::site (
  $major,
  $minor,
  $branch               = " ",
  $bugz_dbname          =  regsubst($title, '([^.-]+)([.-]+)', '\1_', 'G'),
  $bugz_user            = $bugzilla::dbuser,
  $bugz_password        = $bugzilla::dbuserPassword,
  $www_url              = "$title",
  $version              = "$title",
  $branchTag            = "$title",
  $custom_wf            = false,
  $custom_wf_and_status = false,
  $xmlrpc_enabled       = true,
  $base                 = $bugzilla::bugzillaBase,
  $envtype              = "bugzilla",
  $envid                = "$title",
  $userOwner            = $bugzilla::userOwner,
  $userGroup            = $bugzilla::userGroup,
  $envversion           = "${major}.${minor}",
  $envdefault           = false,
  $envdefault_rest      = false,
  $desciptorfile        = " ",
  $usebugaliases        = false,
  $clearMode            = $bugzilla::clearMode,
  $rest_enabled         = false,
  $envinfo              = "",
  ) {

  include "bugzilla"
  $propertyanz = 0
  $confDir = "$base/conf.d"

  if $branch == "trunk" {
    $envinfo1 = "trunk"  
  } elsif $custom_wf {
    $envinfo1 = "Custom Workflow"  
  } elsif $custom_wf_and_status {
    $envinfo1 = "Custom Workflow and Status"  
  } elsif $usebugaliases {
    $envinfo1 = "Use Bugaliases"  
  } else {
    $envinfo1 = ""  
  }
  if $envinfo1 != "" {
    if !$xmlrpc_enabled {
      $envinfo2 = "$envinfo1, XML-RPC disabled"
    } else {
       $envinfo2 = "$envinfo1"
    }
  } else {
    if !$xmlrpc_enabled {
      $envinfo2 = "XML-RPC disabled"
    } else {
       $envinfo2 = ""
    }
  }
  if $envinfo2 != "" {
    if $rest_enabled {
      $envinfo3 = "$envinfo2, REST enabled"
    } else {
       $envinfo3 = "$envinfo2"
    }
  } else {
    if $rest_enabled {
      $envinfo3 = "REST enabled"
    } else {
       $envinfo3 = ""
    }
  }
  if $envinfo != "" {
      $envinfo_intern = $envinfo
  } else {
      $envinfo_intern = $envinfo3
  }
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
#
# always delete existing trunk folders to get the latest version from the trunk
#
    exec { "delete bugzilla $version":
      command => "rm -R $base/$version",
      cwd     => "$base",
      user => "root",
      timeout => 300,
      logoutput => true,
      require   => Exec["prepare bugzilla"]
    }
    exec { "extract bugzilla $version":
      command => "bzr co bzr://bzr.mozilla.org/bugzilla/$branchName $version",
      cwd     => "$base",
      user => "$userOwner",
      timeout => 300,
      creates => "$base/$version",
      require   => Exec["delete bugzilla $version"]
    }
  } else {
    exec { "extract bugzilla $version":
      command => "bzr co -r tag:$branchTag bzr://bzr.mozilla.org/bugzilla/$branchName $version",
      cwd     => "$base",
      user => "$userOwner",
      timeout => 300,
      creates => "$base/$version",
      require   => Exec["prepare bugzilla"]
    }
  }
 
  file { "$base/$version/installPerlModules.sh":
    content => template('bugzilla/installPerlModules.sh.erb'),
    owner   => "$userOwner",
    group   => "$userGroup",
    mode    => 0755,
    require => Exec["extract bugzilla $version"],
  }

  exec { "post extract bugzilla $version":
    command => "$base/$version/installPerlModules.sh  >$base/$version/CGI.out",
    cwd     => "$base/$version",
    creates => "$base/$version/CGI.out",
    user => "$userOwner",
    timeout => 300,
    require   => File["$base/$version/installPerlModules.sh"]
  }  
  
  exec { "mysql-grant-${bugz_dbname}-${bugzilla::dbuser}":
    unless    => 
    "/usr/bin/mysql --user=root --batch -e \"SELECT user FROM db WHERE Host='localhost' and Db='${bugz_dbname}' and User='${bugzilla::dbuser}'\" mysql | /bin/grep '${bugzilla::dbuser}'",
    command   => "/usr/bin/mysql --verbose --user=root -e \"GRANT ALL ON ${bugz_dbname}.* TO '${bugzilla::dbuser}'@localhost\" \
        		; /usr/bin/mysqladmin --verbose --user=root flush-privileges",
    require   => Exec["post extract bugzilla $version"]
  }

  if $clearMode == "clear" {
    exec { "mysql-dropdb-$version":
      onlyif    => "/usr/bin/mysql --user=root '${bugz_dbname}'",
      command   => "/usr/bin/mysqladmin -v --user=root --force drop '${bugz_dbname}'",
      require   => Exec["mysql-grant-${bugz_dbname}-${bugzilla::dbuser}"]
    }

    exec { "mysql-createdb-$version":
      unless    => "/usr/bin/mysql --user=root '${bugz_dbname}'",
      command   => "/usr/bin/mysqladmin -v --user=root --force create '${bugz_dbname}'",
      require   => Exec["mysql-dropdb-$version"]
    }
  } else {
    exec { "mysql-createdb-$version":
      unless    => "/usr/bin/mysql --user=root '${bugz_dbname}'",
      command   => "/usr/bin/mysqladmin -v --user=root --force create '${bugz_dbname}'",
      require   => Exec["mysql-grant-${bugz_dbname}-${bugzilla::dbuser}"]
    }
  }

  file { "$base/$version/callchecksetup.pl":
    content => template('bugzilla/callchecksetup.pl.erb'),
    owner   => "$userOwner",
    group   => "$userGroup",
    mode    => 0755,
    require => Exec["post extract bugzilla $version"],
  }

  file { "$base/$version/answers":
    content => template('bugzilla/answers.erb'),
    owner   => "$userOwner",
    group   => "$userGroup",
    require => Exec["post extract bugzilla $version"],
  }

  file { "$base/$version/extensions/Mylyn":
    ensure  => directory, # so make this a directory
    recurse => true, # enable recursive directory management
    purge   => true, # purge all unmanaged junk
    force   => true, # also purge subdirs and links etc.
    owner   => "$userOwner",
    group   => "$userGroup",
    source  => "puppet:///modules/bugzilla/extensions/Mylyn",
    require => Exec["post extract bugzilla $version"],
  }

  file { "$base/$version/extensions/Mylyn/Extension.pm":
    content => template('bugzilla/Extension.pm.erb'),
    require => File["$base/$version/extensions/Mylyn"],
    owner   => "$userOwner",
    group   => "$userGroup",
    mode    => 0644,
  }

  exec { "init bugzilla_checksetup $version":
    command => "$base/$version/callchecksetup.pl",
    cwd     => "$base/$version",
    creates => "$base/$version/localconfig",
#    user => "$userOwner",
    logoutput => true,
    require => [
      Exec["mysql-createdb-$version"],
      File["$base/$version/answers"],
      File["$base/$version/callchecksetup.pl"],
      File["$base/$version/extensions/Mylyn/Extension.pm"]]
  }

  exec { "update bugzilla_checksetup $version":
    command   => "$base/$version/callchecksetup.pl",
    cwd       => "$base/$version",
    logoutput => true,
#    user => "$userOwner",
    require   => [
      Exec["mysql-createdb-$version"],
      Exec["init bugzilla_checksetup $version"],
      File["$base/$version/answers"],
      File["$base/$version/extensions/Mylyn/Extension.pm"],
      ]
  }

  if !$xmlrpc_enabled {
    file { "$base/$version/xmlrpc.cgi":
      content => template('bugzilla/xmlrpc.cgi.erb'),
      owner   => "$userOwner",
      group   => "$userGroup",
      mode    => 755,
      require => Exec["update bugzilla_checksetup $version"],
    }
  }

  file { "$base/$version/service.json":
    content => template('bugzilla/service.json.erb'),
    owner   => "$userOwner",
    group   => "$userGroup",
    mode    => 644,
    require =>  Exec["update bugzilla_checksetup $version"],
  }

  file { "$confDir/$version.conf":
    content => template('bugzilla/apache2.conf.erb'),
    require => [Package["apache2"], Exec["update bugzilla_checksetup $version"]],
    notify  => Service["apache2"],
  }

  exec { "add $version to /etc/apache2/conf.d/bugzilla.conf":
    command => "echo 'Include $base/conf.d/[^.#]*\n' >> /etc/apache2/conf.d/bugzilla.conf",
    require => File["$confDir/$version.conf"],
    notify  => Service["apache2"],
    onlyif  => "grep -qe '^Include $base/conf.d' /etc/apache2/conf.d/bugzilla.conf; test $? != 0"
  }
}
