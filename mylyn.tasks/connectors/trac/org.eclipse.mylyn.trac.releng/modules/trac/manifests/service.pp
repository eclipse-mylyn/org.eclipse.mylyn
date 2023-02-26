define trac::service (
  $envid      = "$title",
  $version,
  $envtype    = "trac",
  $envinfo    = "",
  $envdefault = false,
  $envmode    = "XML-RPC",
  $accessmode = "XML_RPC",
  $base       = $trac::base,
  $userOwner  = $trac::userOwner,
  $userGroup  = $trac::userGroup,) {
  $envbase = "$base/var/$envid"

  file { "$envbase/service-$title.json":
    content => template('trac/service.json.erb'),
    require => File["$envbase"],
    owner   => "$userOwner",
    group   => "$userGroup",
  }
}
