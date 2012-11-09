Exec {
  path => ["/bin/", "/sbin/", "/usr/bin/", "/usr/sbin/"] }

class bugzilla {
  $dbuser = 'bugz'
  $dbuserPassword = 'ovlwq8'

  $toolsDir = "/home/tools"
  $bugzillaBase = "/home/tools/bugzilla"
  $installHelper = "$bugzillaBase/installHelper"
  $installLog = "$bugzillaBase/installLog"
  $confDir = "$bugzillaBase/conf.d"

  exec { "apt-get update":
    command => "apt-get update",
    onlyif  => "find /var/lib/apt/lists/ -mtime -7 | (grep -q Package; [ $? != 0 ])",
  }

  bugzilla::defaultsites { "bugzilla":
  }

  bugzillaVersion { "bugs36":
    major   => "3",
    minor   => "6",
    require => [Service["mysql"], Exec["mysql-create-user-${dbuser}"]]
  }

  bugzillaVersion { "bugs36-custom-wf":
    major       => "3",
    minor       => "6",
    branch      => "3.6",
    bugz_dbname => "bugs_3_6_cwf",
    custom_wf   => true,
    require     => [Service["mysql"], Exec["mysql-create-user-${dbuser}"]]
  }

  bugzillaVersion { "bugs36-custom-wf-and-status":
    major                => "3",
    minor                => "6",
    branch               => "3.6",
    bugz_dbname          => "bugs_3_6_cwf_ws",
    custom_wf_and_status => true,
    require              => [Service["mysql"], Exec["mysql-create-user-${dbuser}"]]
  }

  bugzillaVersion { "bugs36-xml-rpc-disabled":
    major          => "3",
    minor          => "6",
    branch         => "3.6",
    bugz_dbname    => "bugs_3_6_norpc",
    xmlrpc_enabled => false,
    require        => [Service["mysql"], Exec["mysql-create-user-${dbuser}"]]
  }

  bugzillaVersion { "bugs40":
    major   => "4",
    minor   => "0",
    require => [Service["mysql"], Exec["mysql-create-user-${dbuser}"]]
  }

  bugzillaVersion { "bugs42":
    major   => "4",
    minor   => "2",
    require => [Service["mysql"], Exec["mysql-create-user-${dbuser}"]]
  }

  bugzillaVersion { "bugs44":
    major     => "4",
    minor     => "4",
    branch    => "4.4",
    branchTag => "trunk",
    require   => [Service["mysql"], Exec["mysql-create-user-${dbuser}"]]
  }

  bugzillaVersion { "bugshead":
    major       => "4",
    minor       => "5",
    branch      => "trunk",
    branchTag   => "trunk",
    bugz_dbname => "bugs_head",
    require     => [Service["mysql"], Exec["mysql-create-user-${dbuser}"]]
  }

  file { "/etc/apache2/conf.d/bugzilla.conf":
    content => "Include /home/tools/bugzilla/conf.d/[^.#]*\n",
    require => Package["apache2"],
    notify  => Service["apache2"],
  }

}

include bugzilla
