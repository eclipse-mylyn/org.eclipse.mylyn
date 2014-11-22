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

  bugzilla::site { "bugzilla-4.0.15":
    major   => "4",
    minor   => "0",
  }

  bugzilla::site { "bugzilla-4.2.11":
    major   => "4",
    minor   => "2",
  }

  bugzilla::site { "bugzilla-4.2.11-bugaliases":
    major   => "4",
    minor   => "2",
    branchTag      => "bugzilla-4.2.11",
    usebugaliases => true,
  }

  bugzilla::site { "bugzilla-4.4.6":
    major     => "4",
    minor     => "4",
    envdefault => true,
  }

  bugzilla::site { "bugzilla-4.4.6-custom-wf":
    major       => "4",
    minor       => "4",
    branchTag   => "bugzilla-4.4.6",
    custom_wf   => true,
  }

  bugzilla::site { "bugzilla-4.4.6-custom-wf-and-status":
    major                => "4",
    minor                => "4",
    branchTag            => "bugzilla-4.4.6",
    custom_wf_and_status => true,
  }

/*
  bugzilla::site { "bugzilla-master":
    major       => "5",
    minor       => "1",
    branchTag   => "HEAD",
    envversion   => "5.1",
  }

  bugzilla::site { "bugzilla-rest-master":
    major        => "5",
    minor        => "1",
    branchTag   => "HEAD",
    envversion   => "5.1",
    envtype      => "bugzillaREST",
    rest_enabled => true,
  }
*/
  bugzilla::site { "bugzilla-5.0":
    major       => "5",
    minor       => "0",
    branch      => "5.0",
    branchTag   => "5.0",
    envversion   => "4.5.6+",
  }

  bugzilla::site { "bugzilla-5.0-rest":
    major       => "5",
    minor       => "0",
    branch      => "5.0",
    branchTag   => "5.0",
    envtype      => "bugzillaREST",
    rest_enabled => true,
    envdefault_rest => true,
    envversion   => "4.5.6+",
  }

}