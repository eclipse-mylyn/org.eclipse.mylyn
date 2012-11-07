define hudson(
	$version = "$title",
	$type,
	$qualifier = "",
	$base = "/home/tools/hudson",
) {  
	Exec { path => [ "/bin/", "/sbin/" , "/usr/bin/", "/usr/sbin/" ] }	
	
	exec { "prepare $version":
		command => "mkdir -p $base/archive $base/conf.d",
		creates => "$base/archive",
	}
	
	if $type == "hudson" {
	  if $qualifier == "eclipse" {
	    $url = "http://www.eclipse.org/downloads/download.php?r=1&file=/hudson/war/hudson-${version}.war"
	  } else {
	    $url = "http://java.net/projects/hudson/downloads/download/war/hudson-${version}.war"
	  }
	} elsif $type == "jenkins" {
	  if $qualifier == "stable" {
	    $url = "http://mirrors.jenkins-ci.org/war-stable/${version}/jenkins.war"
	  } else {
	    $url = "http://mirrors.jenkins-ci.org/war/${version}/jenkins.war"
	  }
	} else {
	  fail("unknown type: $type")
	}
	
	exec { "download $version":
    command => "wget -O $base/archive/${type}-$version.war $url",
	  creates => "$base/archive/${type}-$version.war",
	  require => Exec["prepare $version"],
	}
	
}