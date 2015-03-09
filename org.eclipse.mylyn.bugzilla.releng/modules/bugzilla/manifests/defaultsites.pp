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

  bugzilla::site { "bugzilla-4.0.17":
    major   => "4",
    minor   => "0",
  }

  bugzilla::site { "bugzilla-4.2.13":
    major   => "4",
    minor   => "2",
  }

  bugzilla::site { "bugzilla-4.2.13-bugaliases":
    major         => "4",
    minor         => "2",
    branchTag     => "bugzilla-4.2.13",
    usebugaliases => true,
  }

  bugzilla::site { "bugzilla-4.4.8":
    major      => "4",
    minor      => "4",
    envdefault => true,
  }

  bugzilla::site { "bugzilla-4.4.8-custom-wf":
    major       => "4",
    minor       => "4",
    branchTag   => "bugzilla-4.4.8",
    custom_wf   => true,
  }

  bugzilla::site { "bugzilla-4.4.8-custom-wf-and-status":
    major                => "4",
    minor                => "4",
    branchTag            => "bugzilla-4.4.8",
    custom_wf_and_status => true,
  }

  bugzilla::site { "bugzilla-5.0rc2":
    major       => "5",
    minor       => "0",
    branch      => "5.0",
    envversion  => "5.0rc2",
  }

  bugzilla::site { "bugzilla-master":
    major       => "5",
    minor       => "1",
    branchTag   => "HEAD",
    envversion  => "5.1",
    envinfo     => "Master",
  }

/*******************************************************************************
 * REST sites
 *******************************************************************************/

  bugzilla::site { "bugzilla-5.0rc2-rest":
    major           => "5",
    minor           => "0",
    branch          => "5.0",
    branchTag       => "bugzilla-5.0rc2",
    envtype         => "bugzillaREST",
    rest_enabled    => true,
    envdefault_rest => true,
    envversion      => "5.0rc2",
    testdataVersion => "Version1",
  }
 
  bugzilla::site { "bugzilla-rest-master":
    major           => "5",
    minor           => "1",
    branchTag       => "HEAD",
    envversion      => "5.1",
    envinfo         => "Master",
    envtype         => "bugzillaREST",
    rest_enabled    => true,
    testdataVersion => "Version1",
  }

}