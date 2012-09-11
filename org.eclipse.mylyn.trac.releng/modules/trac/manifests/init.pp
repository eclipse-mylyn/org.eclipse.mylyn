define trac(
	$version = "$title",
	$base = "/home/tools/trac"
) {  
	$srcbase = "$base/src/trac-$version"
	$prefix = "$base/share/trac-$version"

	Exec { path => [ "/bin/", "/sbin/" , "/usr/bin/", "/usr/sbin/" ] }	
	
	exec { "prepare $version":
		command => "mkdir -p $base/bin $base/conf.d $base/src $base/var $prefix",
		creates => "$prefix",
	}
	
	file { "$srcbase":
		ensure => "directory",
		require => Exec["prepare $version"]
	}
	if $version == "trunk" {
		exec { "extract trac $version":
	    	command => "svn checkout http://svn.edgewall.org/repos/trac/trunk Trac-trunk",
	    	cwd => "$srcbase",
	    	creates => "$srcbase/Trac-$version",
	    	require => File["$srcbase"],
		}		
	} else {
		exec { "download trac $version":
	    	command => "wget -O $srcbase/Trac-$version.tar.gz http://download.edgewall.org/trac/Trac-$version.tar.gz",
	    	creates => "$srcbase/Trac-$version.tar.gz",
	    	require => File["$srcbase"],
		}
		
		exec { "extract trac $version":
	    	command => "tar -C $srcbase -xzvf $srcbase/Trac-$version.tar.gz",
	    	require => Exec["download trac $version"],
	    	creates => "$srcbase/Trac-$version",
		}
	}


	file { "$srcbase/install.sh": 
    	source => "puppet:///modules/trac/install.sh", 
    	mode => '755', 
  	}
  	
    exec { "install $version":
        command => "$srcbase/install.sh $srcbase/Trac-$version $prefix $version",
        path => ".",
        logoutput => false,
		require => Exec["extract trac $version"],
		creates => "$prefix/lib/.provisioned",
    }

	file { "$base/bin/trac-$version.cgi":
    	content => template('trac/trac.cgi.erb'),
		require => Exec["prepare $version"],
		mode => 755,
	}

	file { "$base/bin/tracadmin-$version":
    	content => template('trac/tracadmin.erb'),
		mode => 755,
		require => Exec["prepare $version"]
	}

}