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
define bugzilla::defaultsites($base = $bugzilla::bugzillaBase, $userOwner = $bugzilla::userOwner, $userGroup = $bugzilla::userGroup,) {
  
  include "bugzilla"

  /* Defaults */

  Bugzilla::Site {
    base      => $base,
    userOwner => $userOwner,
    userGroup => $userGroup,
  }
  
  /* Sites */

  bugzilla::site { "bugzilla-4.4.13-bugaliases":
    major         => "4",
    minor         => "4",
    micro         => "13",
    usebugaliases => true,
  }

  bugzilla::site { "bugzilla-4.4.13":
    major      => "4",
    minor      => "4",
    micro      => "13",
    envdefault => true,
  }

  bugzilla::site { "bugzilla-4.4.13-custom-wf":
    major       => "4",
    minor       => "4",
    micro       => "13",
    custom_wf   => true,
  }

  bugzilla::site { "bugzilla-4.4.13-custom-wf-and-status":
    major                => "4",
    minor                => "4",
    micro                => "13",
    custom_wf_and_status => true,
  }

  bugzilla::site { "bugzilla-5.0.4":
    major       => "5",
    minor       => "0",
    micro       => "4",
  }

  bugzilla::site { "bugzilla-master":
    major       => "5",
    minor       => "1",
    micro       => "2",
    branch      => "master",
    branchTag   => "HEAD",
    envversion  => "5.1.2+",
    envinfo     => "Master",
  }

/*******************************************************************************
 * REST sites
 *******************************************************************************/

  bugzilla::site { "bugzilla-rest-5.0.4":
    major           => "5",
    minor           => "0",
    micro           => "4",
    envtype         => "bugzillaREST",
    rest_enabled    => true,
    envdefault_rest => true,
    testdataVersion => "Version1",
  }

  bugzilla::site { "bugzilla-rest-master":
    major           => "5",
    minor           => "1",
    micro           => "2",
    branch          => "master",
    branchTag       => "HEAD",
    envversion      => "5.1.2+",
    envinfo         => "Master",
    envtype         => "bugzillaREST",
    rest_enabled    => true,
    testdataVersion => "Version2",
  }

  bugzilla::site { "bugzilla-rest-apikey-5.0.4":
    major           => "5",
    minor           => "0",
    micro           => "4",
    envtype         => "bugzillaREST",
    rest_enabled    => true,
    testdataVersion => "Version1",
    api_key_enabled => true,
  }

  bugzilla::site { "bugzilla-rest-apikey-master":
    major           => "5",
    minor           => "1",
    micro           => "2",
    branch          => "master",
    branchTag       => "HEAD",
    envversion      => "5.1.2+",
    envinfo         => "Master, APIKEY enabled",
    envtype         => "bugzillaREST",
    rest_enabled    => true,
    testdataVersion => "Version2",
    api_key_enabled => true,
  }

}