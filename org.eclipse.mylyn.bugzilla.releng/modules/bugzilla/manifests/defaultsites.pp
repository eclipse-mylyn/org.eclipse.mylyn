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

  bugzilla::site { "bugzilla-3.6.13":
    major     => "3",
    minor     => "6",
  }

  bugzilla::site { "bugzilla-3.6.13-custom-wf":
    major       => "3",
    minor       => "6",
    branchTag   => "bugzilla-3.6.13",
    custom_wf   => true,
  }

  bugzilla::site { "bugzilla-3.6.13-custom-wf-and-status":
    major                => "3",
    minor                => "6",
    branchTag            => "bugzilla-3.6.13",
    custom_wf_and_status => true,
    xmlrpc_enabled       => false,
    desciptorfile        => "DescriptorFile-custom-wf-and-status.txt"
  }

  bugzilla::site { "bugzilla-3.6.13-xml-rpc-disabled":
    major          => "3",
    minor          => "6",
    branchTag      => "bugzilla-3.6.13",
    xmlrpc_enabled => false,
  }

  bugzilla::site { "bugzilla-4.0.14":
    major   => "4",
    minor   => "0",
  }

  bugzilla::site { "bugzilla-4.2.10":
    major   => "4",
    minor   => "2",
  }

  bugzilla::site { "bugzilla-4.2.10-bugaliases":
    major   => "4",
    minor   => "2",
    branchTag      => "bugzilla-4.2.10",
    usebugaliases => true,
  }

  bugzilla::site { "bugzilla-4.4.5":
    major     => "4",
    minor     => "4",
    envdefault => true,
  }

bugzilla::site { "bugzilla-4.5.5":
    major        => "4",
    minor        => "5",
    branch       => "trunk",
    branchTag      => "bugzilla-4.5.5",
    envversion   => "4.5.5",
  }
 
bugzilla::site { "bugzilla-rest-4.5.5":
    major        => "4",
    minor        => "5",
    branch       => "trunk",
    branchTag      => "bugzilla-4.5.5",
    rest_enabled => true,
    envversion   => "4.5.5",
    envinfo => "4.5.5, REST enabled"
  }
  
  bugzilla::site { "bugzilla-trunk":
    major       => "4",
    minor       => "5",
    branch      => "trunk",
    branchTag   => "trunk",
    envversion   => "4.5.5+",
  }

bugzilla::site { "bugzilla-rest-trunk":
    major        => "4",
    minor        => "5",
    branch       => "trunk",
    branchTag    => "trunk",
    envversion   => "4.5.5+",
    envdefault_rest => true,
    rest_enabled => true,
  }
}