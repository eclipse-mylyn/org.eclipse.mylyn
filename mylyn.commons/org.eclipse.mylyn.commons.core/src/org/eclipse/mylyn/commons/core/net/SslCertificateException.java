/*******************************************************************************
 * Copyright (c) 2009, 2011 BREDEX GmbH.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     BREDEX GmbH - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.core.net;

import java.io.IOException;

/**
 * Indicates that the access to a certificate-file failed.
 *
 * @author Torsten Kalix
 * @since 3.7
 */
public class SslCertificateException extends IOException {

	private static final long serialVersionUID = 1L;

	public SslCertificateException() {
	}

	public SslCertificateException(String message) {
		super(message);
	}

}
