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
    recurse => true,
    require => Exec["stop $envid"],
  }

	if $digestauth {
		file { "$envbase/htpasswd.digest":
    	content => template('hudson/htpasswd.digest.erb'),
			require => File["$envbase"],
		}
	} else {
		file { "$envbase/htpasswd":
	    content => template('hudson/htpasswd.erb'),
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
    require => File["$envbase"],
  }

  file { "$envbase/stop.sh":
    content => template('hudson/stop.sh.erb'),
    mode => 755,
    require => File["$envbase"],
  }

  file { "$envbase/service.json":
    content => template('hudson/service.json.erb'),
    require => File["$envbase"],
  }

  exec { "start $envid":
    command => "$envbase/start.sh",
    cwd => "$envbase",
    require => File["$envbase/start.sh"],
    creates => "$envbase/pid",
  }

  exec { "add $envbase to apache":
    command => "echo 'Include $base/conf.d/[^.#]*\n' >> /etc/apache2/conf.d/hudson.conf",
    require => File["$conf/$envid.conf"],
    notify  => Service["apache2"],
    onlyif => "grep -qe '^Include $base/conf.d' /etc/apache2/conf.d/hudson.conf; test $? != 0"
  }

}
