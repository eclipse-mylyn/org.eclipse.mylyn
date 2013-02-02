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
 
  bugzilla::site { "bugz36":
    major   => "3",
    minor   => "6",
  }

  bugzilla::site { "bugz36-custom-wf":
    major       => "3",
    minor       => "6",
    branch      => "3.6",
    bugz_dbname => "bugs_3_6_cwf",
    custom_wf   => true,
  }

  bugzilla::site { "bugz36-custom-wf-and-status":
    major                => "3",
    minor                => "6",
    branch               => "3.6",
    bugz_dbname          => "bugz_3_6_cwf_ws",
    custom_wf_and_status => true,
  }

  bugzilla::site { "bugz36-xml-rpc-disabled":
    major          => "3",
    minor          => "6",
    branch         => "3.6",
    bugz_dbname    => "bugz_3_6_norpc",
    xmlrpc_enabled => false,
  }

  bugzilla::site { "bugz40":
    major   => "4",
    minor   => "0",
  }

  bugzilla::site { "bugz42":
    major   => "4",
    minor   => "2",
  }

  bugzilla::site { "bugz44":
    major     => "4",
    minor     => "4",
    branchTag => "trunk",
  }

  bugzilla::site { "bugzhead":
    major       => "4",
    minor       => "5",
    branch      => "trunk",
    branchTag   => "trunk",
    bugz_dbname => "bugz_head",
  }

}