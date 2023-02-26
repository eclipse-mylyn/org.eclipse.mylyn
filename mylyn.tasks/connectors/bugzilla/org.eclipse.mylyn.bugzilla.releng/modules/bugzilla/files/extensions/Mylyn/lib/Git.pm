# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at http://mozilla.org/MPL/2.0/.
#
# This Source Code Form is "Incompatible With Secondary Licenses", as
# defined by the Mozilla Public License, v. 2.0.

package Bugzilla::Extension::Mylyn::Git;

use 5.10.1;
use strict;
use warnings;

use parent qw(Bugzilla::BugUrl);

###############################
####        Methods        ####
###############################

sub should_handle {
    my ($class, $uri) = @_;

    # cGit commit: http://git.eclipse.org/c/actf/org.eclipse.actf.ai.git/commit/?id=84a2bb1e7c58fc8f423724d72cf294fd95f9b1c5
    # cGit commit: https://polarsys.org/cgit/cgit.cgi/capella/capella.git/commit/?id=98011c093f70730bbc9b2113a007aad14a11bb46
    return ($uri->path =~ m|^/c(git/cgit\.cgi)?/.*commit/$|
                and $uri->query_param('id') =~ m|^\w{40}$|) ? 1 : 0;
}

sub _check_value {
    my ($class, $uri) = @_;

    $uri = $class->SUPER::_check_value($uri);

    # GitHub HTTP URLs redirect to HTTPS, so just use the HTTPS scheme.
    $uri->scheme('https');

    return $uri;
}

1;

