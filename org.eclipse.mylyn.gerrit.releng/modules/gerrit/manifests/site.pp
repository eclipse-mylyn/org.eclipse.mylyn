define gerrit::site(
	$envid = "$title",
	$port,
	$sshport,
	$version,
  $allbasicauth = false,
  $certauth = false,
  $digestauth = false,
	$base = "/home/tools/gerrit",
	$envtype = "gerrit",
	$envinfo = "",
) { 
	$envbase = "$base/$envid"
	$conf = "$base/conf.d"

  /* can't use cwd => $envbase since that may not yet exist and would cause a cyclic dependency */
  exec { "stop $envid":
    command => "/bin/sh -c '(cd $envbase && $envbase/bin/gerrit.sh stop)'",
    require => Gerrit["$version"],
    onlyif => "test -x $envbase/bin/gerrit.sh && $envbase/bin/gerrit.sh check | grep -q 'Gerrit running'",
  }

  exec { "configure $envid":
    command => "java -jar $base/archive/gerrit-$version.war init --batch --site-path $envbase --no-auto-start",
    require => Exec["stop $envid"],
    creates => "$envbase",
  }

  file { "$envbase/etc/gerrit.config":
    content => template('gerrit/gerrit.config.erb'),
    require => Exec["configure $envid"],
  }
	
	file { "$envbase":
    ensure => "directory",
    require => Exec["configure $envid"]
  }
	
	file { "$conf/$envid.conf":
    content => template('gerrit/gerrit.conf.erb'),
    notify => Service["apache2"],
	}

	if $digestauth {
		file { "$envbase/htpasswd.digest":
    	content => template('gerrit/htpasswd.digest.erb'),
			require => File["$envbase"],
		}
	} else {
		file { "$envbase/htpasswd":
	    content => template('gerrit/htpasswd.erb'),
			require => File["$envbase"],
		}
	}

  file { "$envbase/admin.id_rsa":
    source => "puppet:///modules/gerrit/admin.id_rsa",
    require => File["$envbase"],
  }

  file { "$envbase/setup.sql":
    source => "puppet:///modules/gerrit/setup.sql",
    require => File["$envbase"],
  }

  file { "$envbase/service.json":
    content => template('gerrit/service.json.erb'),
    require => File["$envbase"],
  }
      
  exec { "create admin user for $envid":
    command => "java -jar bin/gerrit.war gsql < $envbase/setup.sql",
    cwd => "$envbase",
    require => [ Exec["configure $envid"], File["$envbase/setup.sql"], File["$envbase/admin.id_rsa"], ],
  }

  exec { "start $envid":
    command => "$envbase/bin/gerrit.sh start",
    cwd => "$envbase",
    require => [ Exec["create admin user for $envid"], File["$envbase/etc/gerrit.config"] ],
    creates => "$envbase/log/gerrit.pid",
  }
  
  $ssh = "ssh -p $sshport -i $envbase/admin.id_rsa -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no admin@localhost"

  exec { "create project for $envid":
    command => "$ssh gerrit create-project --name org.eclipse.mylyn.test --empty-commit",
    require => Exec["start $envid"],
    creates => "$envbase/git/org.eclipse.mylyn.test.git"
  }

}
