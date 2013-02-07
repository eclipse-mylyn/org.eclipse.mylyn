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
 
  bugzilla::site { "bugs36":
    major   => "3",
    minor   => "6",
  }

  bugzilla::site { "bugs36-custom-wf":
    major       => "3",
    minor       => "6",
    branch      => "3.6",
    bugz_dbname => "bugs_3_6_cwf",
    custom_wf   => true,
  }

  bugzilla::site { "bugs36-custom-wf-and-status":
    major                => "3",
    minor                => "6",
    branch               => "3.6",
    bugz_dbname          => "bugs_3_6_cwf_ws",
    custom_wf_and_status => true,
  }

  bugzilla::site { "bugs36-xml-rpc-disabled":
    major          => "3",
    minor          => "6",
    branch         => "3.6",
    bugz_dbname    => "bugs_3_6_norpc",
    xmlrpc_enabled => false,
  }

  bugzilla::site { "bugs40":
    major   => "4",
    minor   => "0",
  }

  bugzilla::site { "bugs42":
    major   => "4",
    minor   => "2",
    envdefault => "1"
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
    bugz_dbname => "bugs_head",
  }

}