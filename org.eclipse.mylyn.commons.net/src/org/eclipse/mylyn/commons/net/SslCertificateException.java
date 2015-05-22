/*******************************************************************************
 * Copyright (c) 2009, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.net;

import java.io.IOException;

/**
 * Indicates that the access to a certificate-file failed.
 * 
 * @author Torsten Kalix
 * @since 3.6
 */
public class SslCertificateException extends IOException {

	private static final long serialVersionUID = 1L;

	public SslCertificateException() {
	}

	public SslCertificateException(String message) {
		super(message);
	}

}
