define hudson::hudson(
	$version = "$title",
	$type,
	$qualifier = "",
	$base = $hudson::base,
	$userOwner = $hudson::userOwner,
	$userGroup = $hudson::userGroup,
) {

  include "hudson"

	exec { "prepare $version":
		command => "mkdir -p $base/archive $base/conf.d",
		creates => "$base/archive",
		user => "$userOwner",
		require => Exec["prepare hudson"],
	}

	if $type == "hudson" {
	  if $qualifier == "eclipse" {
	    $url = "http://www.eclipse.org/downloads/download.php?r=1&file=/hudson/war/hudson-${version}.war"
    } else {
      $url = "http://java.net/downloads/hudson/war/hudson-${version}.war"
	  }
	} elsif $type == "jenkins" {
	  if $qualifier == "stable" {
	    $url = "http://mirrors.jenkins-ci.org/war-stable/${version}/jenkins.war"
    } elsif $qualifier == "latest" {
      $url = "http://mirrors.jenkins-ci.org/war/latest/jenkins.war"
	  } else {
	    $url = "http://mirrors.jenkins-ci.org/war/${version}/jenkins.war"
	  }
	} else {
	  fail("unknown type: $type")
	}


	exec { "download $version":
    command => "wget -O '$base/archive/${type}-$version.war' '$url'",
      creates => "$base/archive/${type}-$version.war",
      user => "$userOwner",
      require => Exec["prepare $version"],
      timeout => 360,
	}

}
