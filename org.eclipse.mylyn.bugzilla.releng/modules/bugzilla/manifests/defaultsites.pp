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

  bugzilla::site { "bugzilla-4.0.18":
    major   => "4",
    minor   => "0",
    micro   => "18",
  }

  bugzilla::site { "bugzilla-4.2.14":
    major   => "4",
    minor   => "2",
    micro   => "14",
  }

  bugzilla::site { "bugzilla-4.4.9-bugaliases":
    major         => "4",
    minor         => "4",
    micro         => "9",
    usebugaliases => true,
  }

  bugzilla::site { "bugzilla-4.4.9":
    major      => "4",
    minor      => "4",
    micro      => "9",
    envdefault => true,
  }

  bugzilla::site { "bugzilla-4.4.9-custom-wf":
    major       => "4",
    minor       => "4",
    micro       => "9",
    custom_wf   => true,
  }

  bugzilla::site { "bugzilla-4.4.9-custom-wf-and-status":
    major                => "4",
    minor                => "4",
    micro                => "9",
    custom_wf_and_status => true,
  }

  bugzilla::site { "bugzilla-5.0":
    major       => "5",
    minor       => "0",
    micro       => "",
  }
 
  bugzilla::site { "bugzilla-master":
    major       => "5",
    minor       => "1",
    micro       => "",
    branch      => "master",
    branchTag   => "HEAD",
    envversion  => "5.1",
    envinfo     => "Master",
  }

/*******************************************************************************
 * REST sites
 *******************************************************************************/

  bugzilla::site { "bugzilla-rest-5.0-head":
    major           => "5",
    minor           => "0",
    micro           => "",
    branch          => "5.0",
    envtype         => "bugzillaREST",
    rest_enabled    => true,
    envdefault_rest => true,
    testdataVersion => "Version1",
  }
  

  bugzilla::site { "bugzilla-rest-master":
    major           => "5",
    minor           => "1",
    micro           => "",
    branch          => "master",
    branchTag       => "HEAD",
    envversion      => "5.1",
    envinfo         => "Master",
    envtype         => "bugzillaREST",
    rest_enabled    => true,
    testdataVersion => "Version2",
  }

}