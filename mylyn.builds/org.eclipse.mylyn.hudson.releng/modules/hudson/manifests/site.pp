define hudson::site(
	$envid = "$title",
	$envtype,
	$data,
	$port,
	$version,
	$allbasicauth = false,
	$certauth = false,
	$digestauth = false,
	$base = $hudson::base,
	$envinfo = "",
	$envdefault = false,
	$userOwner = $hudson::userOwner,
	$userGroup = $hudson::userGroup,
	$folderPlugin = false,
	$matrixPlugin = false,
) { 
	$envbase = "$base/$envid"
	$conf = "$base/conf.d"

  /* can't use cwd => $envbase since that may not yet exist and would cause a cyclic dependency */
  exec { "stop $envid":
    command => "/bin/sh -c '(cd $envbase && $envbase/stop.sh)'",
    require => Hudson["$version"],
    onlyif => "test -e $envbase/${envtype}.pid",
  }

  file { "$envbase":
    source => "puppet:///modules/hudson/${data}",
    recurse => remote,
    owner   => "$userOwner",
    group   => "$userGroup",
    require => Exec["stop $envid"],
  }

	if $digestauth {
		file { "$envbase/htpasswd.digest":
    	content => template('hudson/htpasswd.digest.erb'),
        owner   => "$userOwner",
        group   => "$userGroup",
			require => File["$envbase"],
		}
	} else {
		file { "$envbase/htpasswd":
	    content => template('hudson/htpasswd.erb'),
        owner   => "$userOwner",
        group   => "$userGroup",
			require => File["$envbase"],
		}
	}

  file { "$conf/$envid.conf":
    content => template('hudson/apache.conf.erb'),
    notify => Service["apache2"],
  }

  file { "$envbase/start.sh":
    content => template('hudson/start.sh.erb'),
    mode => 755,
    owner   => "$userOwner",
    group   => "$userGroup",
    require => File["$envbase"],
  }

  file { "$envbase/stop.sh":
    content => template('hudson/stop.sh.erb'),
    mode => 755,
    owner   => "$userOwner",
    group   => "$userGroup",
    require => File["$envbase"],
  }

  file { "$envbase/service.json":
    content => template('hudson/service.json.erb'),
    owner   => "$userOwner",
    group   => "$userGroup",
    require => File["$envbase"],
  }

  if $folderPlugin {
  	exec { "install folder plugin for $envbase":
	  command => "wget -P $envbase/plugins http://updates.jenkins-ci.org/download/plugins/cloudbees-folder/4.8/cloudbees-folder.hpi",
 	  cwd => "$envbase",
	  user => "$userOwner",
	}
  } else {	
	file { "$envbase/jobs/test-folder/":
      ensure => absent,
      recurse => true,
      backup => false,
	  force => true,
	}
  }
  
  if $matrixPlugin {
	exec { "install JUnit plugin for $envbase":
	  command => "wget -P $envbase/plugins http://updates.jenkins-ci.org/download/plugins/junit/1.20/junit.hpi",
 	  cwd => "$envbase",
	  user => "$userOwner",
	}
	exec { "install script security plugin for $envbase":
	  command => "wget -P $envbase/plugins http://updates.jenkins-ci.org/download/plugins/script-security/1.13/script-security.hpi",
 	  cwd => "$envbase",
	  user => "$userOwner",
	}
  	exec { "install matrix plugin for $envbase":
	  command => "wget -P $envbase/plugins http://updates.jenkins-ci.org/download/plugins/matrix-project/1.7.1/matrix-project.hpi",
 	  cwd => "$envbase",
	  user => "$userOwner",
	}
  } else {	
	file { "$envbase/jobs/test-single-axis/":
      ensure => absent,
      recurse => true,
      backup => false,
	  force => true,
	}
	
	file { "$envbase/jobs/test-multi-axis/":
      ensure => absent,
      recurse => true,
      backup => false,
	  force => true,
	}
  }
  
  exec { "start $envid":
    command => "$envbase/start.sh",
    cwd => "$envbase",
    require => File["$envbase/start.sh"],
    user   => "$userOwner",
     creates => "$envbase/pid",
  }

  exec { "add $envbase to apache":
    command => "echo 'Include $base/conf.d/[^.#]*\n' >> /etc/apache2/conf-enabled/hudson.conf",
    require => File["$conf/$envid.conf"],
    notify  => Service["apache2"],
    onlyif => "grep -qe '^Include $base/conf.d' /etc/apache2/conf-enabled/hudson.conf; test $? != 0"
  }

}
