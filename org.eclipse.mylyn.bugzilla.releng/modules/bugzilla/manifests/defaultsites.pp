/*******************************************************************************
 * Copyright (c) 2012 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *     Steffen Pingel (Tasktop Techologies)
 *******************************************************************************/
define bugzilla::defaultsites($base = $bugzilla::bugzillaBase, $userOwner = $bugzilla::userOwner, $userGroup = $bugzilla::userGroup,) {
  
  include "bugzilla"

  /* Defaults */

  Bugzilla::Site {
    base      => $base,
    userOwner => $userOwner,
    userGroup => $userGroup,
  }
  
  /* Sites */

  bugzilla::site { "bugzilla-3.6.12":
    major     => "3",
    minor     => "6",
  }

  bugzilla::site { "bugs36-custom-wf":
    major       => "3",
    minor       => "6",
    branchTag   => "bugzilla-3.6.12",
    custom_wf   => true,
  }

  bugzilla::site { "bugs36-custom-wf-and-status":
    major                => "3",
    minor                => "6",
    branchTag            => "bugzilla-3.6.12",
    custom_wf_and_status => true,
  }

  bugzilla::site { "bugs36-xml-rpc-disabled":
    major          => "3",
    minor          => "6",
    branchTag      => "bugzilla-3.6.12",
    xmlrpc_enabled => false,
  }

  bugzilla::site { "bugzilla-4.0.10":
    major   => "4",
    minor   => "0",
  }

  bugzilla::site { "bugzilla-4.2.5":
    major   => "4",
    minor   => "2",
    envdefault => "1",
  }

  bugzilla::site { "bugs44":
    major     => "4",
    minor     => "4",
    branchTag => "trunk",
  }

  bugzilla::site { "bugshead":
    major       => "4",
    minor       => "5",
    branch      => "trunk",
    branchTag   => "trunk",
  }
}