/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core.client;

/**
 * @author Steffen Pingel
 */
public class TracSslCertificateException extends TracException {

	private static final long serialVersionUID = -693879319319751584L;

	public TracSslCertificateException() {
		super("Opening of the certificate keystore failed"); //$NON-NLS-1$
	}

}
