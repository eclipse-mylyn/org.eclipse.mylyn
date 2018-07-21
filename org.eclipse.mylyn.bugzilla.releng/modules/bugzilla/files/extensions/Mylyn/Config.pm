# -*- Mode: perl; indent-tabs-mode: nil -*-
#
# Copyright (c) 2012 Frank Becker and others.
# 
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License v. 2.0 which is available at
# https://www.eclipse.org/legal/epl-2.0
# 
# SPDX-License-Identifier: EPL-2.0
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