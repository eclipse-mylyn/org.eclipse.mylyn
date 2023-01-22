/*******************************************************************************
 * Copyright (c) 2012 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *     Steffen Pingel (Tasktop Techologies)
 *******************************************************************************/
Exec {
  path => ["/bin/", "/sbin/", "/usr/bin/", "/usr/sbin/"] }

user { "tools":
  ensure     => present,
  membership => minimum,
  shell      => "/bin/bash",
  managehome => true,
}

include "bugzilla"

exec { "disable all":
  command => "find $bugzilla::bugzillaBase -name \"service*.json\" | xargs -i mv {} {}.disabled",
  onlyif  => "test -e $bugzilla::bugzillaBase",
}

bugzilla::defaultsites { "bugzilla":
}