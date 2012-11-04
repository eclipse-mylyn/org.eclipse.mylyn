define gerrit(
	$version = "$title",
	$base = "/home/tools/gerrit",
	$postfix = "",
) {  
	Exec { path => [ "/bin/", "/sbin/" , "/usr/bin/", "/usr/sbin/" ] }	
	
	exec { "prepare $version":
		command => "mkdir -p $base/archive $base/conf.d",
		creates => "$base/archive",
	}
	
	exec { "download gerrit $version":
    command => "wget -O $base/archive/gerrit-$version.war https://gerrit.googlecode.com/files/gerrit${postfix}-${version}.war",
	  creates => "$base/archive/gerrit-$version.war",
	  require => Exec["prepare $version"],
	}
	
}