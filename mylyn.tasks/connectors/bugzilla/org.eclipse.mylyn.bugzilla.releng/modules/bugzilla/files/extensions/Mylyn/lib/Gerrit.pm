# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at http://mozilla.org/MPL/2.0/.
#
# This Source Code Form is "Incompatible With Secondary Licenses", as
# defined by the Mozilla Public License, v. 2.0.

package Bugzilla::Extension::Mylyn::Gerrit;

use 5.10.1;
use strict;
use warnings;

use parent qw(Bugzilla::BugUrl);

###############################
####        Methods        ####
###############################

sub should_handle {
    my ($class, $uri) = @_;

    # Gerrit Change URL: https://git.eclipse.org/r/#/c/26613/
    # Gerrit Change URL, specific patch set: https://git.eclipse.org/r/#/c/26613/4
    # https://git.eclipse.org/r/40031
    return ( ($uri->path =~ m|^/r/$| and $uri->fragment =~ m|^/c/\d+|) ||
                $uri->path =~ m|^/r/\d+|) ? 1 : 0;
}

sub _check_value {
    my ($class, $uri) = @_;

    $uri = $class->SUPER::_check_value($uri);

    # While Gerrit URLs can be either HTTP or HTTPS,
    # always go with the HTTP scheme, as that's the default.
    if ($uri->scheme eq 'http') {
        $uri->scheme('https');
    }

    return $uri;
}

1;

