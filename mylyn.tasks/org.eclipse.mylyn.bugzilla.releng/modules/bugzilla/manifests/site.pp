/*******************************************************************************
 * Copyright (c) 2012 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *     Steffen Pingel (Tasktop Techologies)
 *******************************************************************************/
define bugzilla::site (
  $major,
  $minor,
  $micro,
  $branch               = "${major}.${minor}",
  $bugz_dbname          =  regsubst($title, '([^.-]+)([.-]+)', '\1_', 'G'),
  $bugz_user            = $bugzilla::dbuser,
  $bugz_password        = $bugzilla::dbuserPassword,
  $www_url              = "$title",
  $bugzillaDir          = "$title",
  $branchTag            = "",
  $custom_wf            = false,
  $custom_wf_and_status = false,
  $xmlrpc_enabled       = true,
  $base                 = $bugzilla::bugzillaBase,
  $envtype              = "bugzilla",
  $envid                = "$title",
  $userOwner            = $bugzilla::userOwner,
  $userGroup            = $bugzilla::userGroup,
  $envversion           = "",
  $envdefault           = false,
  $envdefault_rest      = false,
  $desciptorfile        = " ",
  $usebugaliases        = false,
  $clearMode            = $bugzilla::clearMode,
  $rest_enabled         = false,
  $api_key_enabled      = false,
  $envinfo              = "",
  $testdataVersion      = "",
  ) {

  include "bugzilla"
  $propertyanz = 0
  $confDir = "$base/conf.d"
  if ($branchTag !="") {
    $branchTagInternal = $branchTag
  } else {
    if ($micro !="") {
      $branchTagInternal = "release-${major}.${minor}.${micro}"
    } else {
      $branchTagInternal = "release-${major}.${minor}"
    }
  }
    if ($envversion !="") {
    $envversionInternal = $envversion
  } else {
    if ($micro !="") {
      $envversionInternal = "${major}.${minor}.${micro}"
    } else {
      $envversionInternal = "${major}.${minor}"
    }
  }
  if $custom_wf {
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
    if $api_key_enabled {
      $envinfo3 = "$envinfo2, APIKEY enabled"
    } else {
       $envinfo3 = "$envinfo2"
    }
  } else {
    if $api_key_enabled {
      $envinfo3 = "APIKEY enabled"
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

  if $branch == "master" {
    if $branchTagInternal == "HEAD" {
      exec { "master master git pull $bugzillaDir":
        command => "git pull",
        onlyif    => "/usr/bin/test -d $base/$bugzillaDir",
        cwd     => "$base/$bugzillaDir",
        timeout => 360,
        logoutput => true,
        require   => Exec["prepare bugzilla"],
        notify => Exec["end extract bugzilla $bugzillaDir"],
      }
      exec { "master master git clone $bugzillaDir":
        command => "git clone https://github.com/bugzilla/bugzilla $base/$bugzillaDir",
        cwd     => "$base",
        timeout => 360,
        creates => "$base/$bugzillaDir",
        require   => Exec["prepare bugzilla"],
        notify => Exec["end extract bugzilla $bugzillaDir"],
      }
    } else {
      exec { "master $branchTagInternal git clone $bugzillaDir":
        command => "git clone -b $branch https://github.com/bugzilla/bugzilla $base/$bugzillaDir",
        cwd     => "$base",
        timeout => 360,
        creates => "$base/$bugzillaDir",
        require   => Exec["prepare bugzilla"],
      }
      exec { "master $branchTagInternal git checkout $bugzillaDir":
        command => "git checkout $branchTagInternal",
        cwd     => "$base/$bugzillaDir",
        logoutput => true,
        timeout => 360,
        require   => Exec["master $branchTagInternal git clone $bugzillaDir"],
        notify => Exec["end extract bugzilla $bugzillaDir"],
      }
    }
  } else {
    exec { "$branch $branchTagInternal git pull $bugzillaDir":
      command => "git reset --hard $branchTagInternal;git pull origin $branchTagInternal",
      onlyif    => "/usr/bin/test -d $base/$bugzillaDir",
      cwd     => "$base/$bugzillaDir",
      timeout => 360,
      logoutput => true,
      require   => Exec["prepare bugzilla"],
      notify => Exec["end extract bugzilla $bugzillaDir"],
    }

    exec { "$branch $branchTagInternal git clone $bugzillaDir":
      command => "git clone -b $branch https://github.com/bugzilla/bugzilla $base/$bugzillaDir",
      cwd     => "$base",
      timeout => 360,
      creates => "$base/$bugzillaDir",
      require   => Exec["$branch $branchTagInternal git pull $bugzillaDir"],
      notify => Exec["end extract bugzilla $bugzillaDir"],
    }
  }

  exec { "end extract bugzilla $bugzillaDir":
      command => "echo 'end extract bugzilla $bugzillaDir $branch $branchTagInternal'",
      logoutput => true,
    }

  file { "$base/$bugzillaDir/installPerlModules.sh":
    content => template('bugzilla/installPerlModules.sh.erb'),
    owner   => "$userOwner",
    group   => "$userGroup",
    mode    => 0755,
    require => Exec["end extract bugzilla $bugzillaDir"],
  }

  exec { "post extract bugzilla $bugzillaDir":
    command => "$base/$bugzillaDir/installPerlModules.sh  >$base/$bugzillaDir/CGI.out",
    cwd     => "$base/$bugzillaDir",
    creates => "$base/$bugzillaDir/CGI.out",
    timeout => 360,
    require   => File["$base/$bugzillaDir/installPerlModules.sh"]
  }

  exec { "mysql-grant-${bugz_dbname}-${bugzilla::dbuser}":
    unless    =>
    "/usr/bin/mysql --user=root --batch -e \"SELECT user FROM db WHERE Host='localhost' and Db='${bugz_dbname}' and User='${bugzilla::dbuser}'\" mysql | /bin/grep '${bugzilla::dbuser}'",
    command   => "/usr/bin/mysql --verbose --user=root -e \"GRANT ALL ON ${bugz_dbname}.* TO '${bugzilla::dbuser}'@localhost\" \
        		; /usr/bin/mysqladmin --verbose --user=root flush-privileges",
    require   => Exec["post extract bugzilla $bugzillaDir"]
  }

  if $clearMode == "clear" {
    exec { "mysql-dropdb-$bugzillaDir":
      onlyif    => "/usr/bin/mysql --user=root '${bugz_dbname}'",
      command   => "/usr/bin/mysqladmin -v --user=root --force drop '${bugz_dbname}'",
      require   => Exec["mysql-grant-${bugz_dbname}-${bugzilla::dbuser}"]
    }

    exec { "mysql-createdb-$bugzillaDir":
      unless    => "/usr/bin/mysql --user=root '${bugz_dbname}'",
      command   => "/usr/bin/mysqladmin -v --user=root --force create '${bugz_dbname}'",
      require   => Exec["mysql-dropdb-$bugzillaDir"]
    }
  } else {
    exec { "mysql-createdb-$bugzillaDir":
      unless    => "/usr/bin/mysql --user=root '${bugz_dbname}'",
      command   => "/usr/bin/mysqladmin -v --user=root --force create '${bugz_dbname}'",
      require   => Exec["mysql-grant-${bugz_dbname}-${bugzilla::dbuser}"]
    }
  }

  file { "$base/$bugzillaDir/callchecksetup.pl":
    content => template('bugzilla/callchecksetup.pl.erb'),
    owner   => "$userOwner",
    group   => "$userGroup",
    mode    => 0755,
    require => Exec["post extract bugzilla $bugzillaDir"],
  }

  file { "$base/$bugzillaDir/answers":
    content => template('bugzilla/answers.erb'),
    owner   => "$userOwner",
    group   => "$userGroup",
    require => Exec["post extract bugzilla $bugzillaDir"],
  }

  file { "$base/$bugzillaDir/extensions/Mylyn":
    ensure  => directory, # so make this a directory
    recurse => true, # enable recursive directory management
    purge   => true, # purge all unmanaged junk
    force   => true, # also purge subdirs and links etc.
    owner   => "$userOwner",
    group   => "$userGroup",
    source  => "puppet:///modules/bugzilla/extensions/Mylyn",
    require => Exec["post extract bugzilla $bugzillaDir"],
  }

  file { "$base/$bugzillaDir/extensions/Mylyn/Extension.pm":
    content => template('bugzilla/Extension.pm.erb'),
    require => File["$base/$bugzillaDir/extensions/Mylyn"],
    owner   => "$userOwner",
    group   => "$userGroup",
    mode    => 0644,
  }

  exec { "init bugzilla_checksetup $bugzillaDir":
    command => "$base/$bugzillaDir/checksetup.pl $base/$bugzillaDir/answers || exit 0",
    cwd     => "$base/$bugzillaDir",
    creates => "$base/$bugzillaDir/localconfig",
    logoutput => true,
    require => [
      Exec["mysql-createdb-$bugzillaDir"],
      File["$base/$bugzillaDir/answers"],
      File["$base/$bugzillaDir/callchecksetup.pl"],
      File["$base/$bugzillaDir/extensions/Mylyn/Extension.pm"]]
  }


  exec { "update bugzilla_checksetup $bugzillaDir":
    command => "$base/$bugzillaDir/checksetup.pl $base/$bugzillaDir/answers || exit 0",
    cwd       => "$base/$bugzillaDir",
    logoutput => true,
    require   => [
      Exec["mysql-createdb-$bugzillaDir"],
      Exec["init bugzilla_checksetup $bugzillaDir"],
      File["$base/$bugzillaDir/answers"],
      File["$base/$bugzillaDir/extensions/Mylyn/Extension.pm"],
      ]
  }

  if !$xmlrpc_enabled {
    file { "$base/$bugzillaDir/xmlrpc.cgi":
      content => template('bugzilla/xmlrpc.cgi.erb'),
      owner   => "$userOwner",
      group   => "$userGroup",
      mode    => 755,
      require => Exec["update bugzilla_checksetup $bugzillaDir"],
    }
  }

  file { "$base/$bugzillaDir/service.json":
    content => template('bugzilla/service.json.erb'),
    owner   => "$userOwner",
    group   => "$userGroup",
    mode    => 644,
    require =>  Exec["update bugzilla_checksetup $bugzillaDir"],
  }

  file { "$confDir/$bugzillaDir.conf":
    content => template('bugzilla/apache2.conf.erb'),
    require => [Package["apache2"], Exec["update bugzilla_checksetup $bugzillaDir"]],
    notify  => Service["apache2"],
  }

  exec { "add $bugzillaDir to /etc/apache2/conf-enabled/bugzilla.conf":
    command => "echo 'Include $base/conf.d/[^.#]*\n' >> /etc/apache2/conf-enabled/bugzilla.conf",
    require => File["$confDir/$bugzillaDir.conf"],
    notify  => Service["apache2"],
    onlyif  => "grep -qe '^Include $base/conf.d' /etc/apache2/conf-enabled/bugzilla.conf; test $? != 0"
  }

}