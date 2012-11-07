# -*- Mode: perl; indent-tabs-mode: nil -*-
#
# Copyright (c) 2012 Frank Becker and others.
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
#
# Contributors:
#     Frank Becker - initial API and implementation
#

package Bugzilla::Extension::Mylyn;
use strict;

use constant NAME => 'Mylyn';

use constant REQUIRED_MODULES => [
    {
        package => 'Data-Dumper',
        module  => 'Data::Dumper',
        version => 0,
    },
];

use constant OPTIONAL_MODULES => [
];

__PACKAGE__->NAME;