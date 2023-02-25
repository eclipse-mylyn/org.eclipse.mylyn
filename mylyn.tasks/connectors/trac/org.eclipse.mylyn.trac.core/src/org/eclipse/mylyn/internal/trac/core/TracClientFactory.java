/*******************************************************************************
 * Copyright (c) 2006, 2008 Steffen Pingel and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core;

import java.net.MalformedURLException;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient.Version;
import org.eclipse.mylyn.internal.trac.core.client.TracException;
import org.eclipse.mylyn.internal.trac.core.client.TracLoginException;
import org.eclipse.mylyn.internal.trac.core.client.TracWebClient;
import org.eclipse.mylyn.internal.trac.core.client.TracXmlRpcClient;

/**
 * @author Steffen Pingel
 */
public class TracClientFactory {

	public static ITracClient createClient(AbstractWebLocation location, Version version) {
		if (version == Version.TRAC_0_9) {
			return new TracWebClient(location, version);
		} else if (version == Version.XML_RPC) {
			return new TracXmlRpcClient(location, version);
		}

		// fall-back to XML_RPC in case the repository information is incomplete
		return new TracXmlRpcClient(location, Version.XML_RPC);
	}

	/**
	 * Tries all supported access types for <code>location</code> and returns the corresponding version if successful;
	 * throws an exception otherwise.
	 * <p>
	 * Order of the tried access types: XML-RPC, Trac 0.9
	 */
	public static Version probeClient(AbstractWebLocation location) throws MalformedURLException, TracException {
		try {
			ITracClient repository = new TracXmlRpcClient(location, Version.XML_RPC);
			repository.validate(new NullProgressMonitor());
			return Version.XML_RPC;
		} catch (TracException e) {
			try {
				ITracClient repository = new TracWebClient(location, Version.TRAC_0_9);
				repository.validate(new NullProgressMonitor());
				return Version.TRAC_0_9;
			} catch (TracLoginException e2) {
				throw e;
			} catch (TracException e2) {
			}
		}

		throw new TracException();
	}

}
