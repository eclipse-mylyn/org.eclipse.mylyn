define trac::plugin(
	$plugin = "$title",
	$egg,
	$url,
	$base = $trac::base,
) {
	$srcbase = "$base/src/$plugin"
	
	include "trac"
	
	exec { "prepare $plugin":
    command => "mkdir -p $srcbase",
    creates => "$srcbase",
    require => Exec["prepare trac"]
  }
	
	exec { "svn checkout $plugin":
    	command => "svn checkout $url src",
    	cwd => "$srcbase",
    	creates => "$srcbase/src",
    	require => Exec["prepare $plugin"],
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