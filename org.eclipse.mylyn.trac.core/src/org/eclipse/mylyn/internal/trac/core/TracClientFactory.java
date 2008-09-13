/*******************************************************************************
* Copyright (c) 2006, 2008 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core;

import java.net.MalformedURLException;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient;
import org.eclipse.mylyn.internal.trac.core.client.TracException;
import org.eclipse.mylyn.internal.trac.core.client.TracLoginException;
import org.eclipse.mylyn.internal.trac.core.client.TracWebClient;
import org.eclipse.mylyn.internal.trac.core.client.TracXmlRpcClient;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient.Version;

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

		throw new RuntimeException("Invalid repository version: " + version);
	}

	/**
	 * Tries all supported access types for <code>location</code> and returns the corresponding version if successful;
	 * throws an exception otherwise.
	 * 
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
