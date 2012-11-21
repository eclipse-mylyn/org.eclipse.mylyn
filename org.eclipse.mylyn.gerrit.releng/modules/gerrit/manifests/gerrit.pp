define gerrit::gerrit(
	$version = "$title",
	$base = "/home/tools/gerrit",
	$postfix = "",
) {  

  include "gerrit"
  
	Exec { path => [ "/bin/", "/sbin/" , "/usr/bin/", "/usr/sbin/" ] }	
	
	exec { "prepare $version":
		command => "mkdir -p $base/archive $base/conf.d",
        user => "$gerrit::userOwner",
		creates => "$base/archive",
	}
	
	exec { "download gerrit $version":
    command => "wget -O $base/archive/gerrit-$version.war https://gerrit.googlecode.com/files/gerrit${postfix}-${version}.war",
	  creates => "$base/archive/gerrit-$version.war",
      user => "$gerrit::userOwner",
	  require => Exec["prepare $version"],
	}
	
}