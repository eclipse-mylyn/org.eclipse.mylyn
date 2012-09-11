define trac::plugin(
	$plugin = "$title",
	$egg,
	$url,
	$base = "/home/tools/trac",
) {
	$srcbase = "$base/src/$plugin"
	
	file { "$srcbase":
		ensure => "directory",
	}
	
	exec { "svn checkout $plugin":
    	command => "svn checkout $url src",
    	cwd => "$srcbase",
    	creates => "$srcbase/src",
    	require => File["$srcbase"],
	}
	
	exec { "setup $plugin":
    	command => "python setup.py bdist_egg",
    	cwd => "$srcbase/src",
    	creates => "$srcbase/src/dist",
    	require => Exec["svn checkout $plugin"],
	}
	
	exec { "copy egg $plugin":
		command => "cp $srcbase/src/dist/${egg}-*.egg $srcbase/src/dist/$egg.egg",
    	creates => "$srcbase/src/dist/$egg.egg",
		require => Exec["setup $plugin"],
	}
		
}