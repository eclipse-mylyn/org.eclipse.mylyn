/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.discovery.core.model;

/**
 * @author Steffen Pingel
 */
public class DiscoveryCertification extends Certification {

	private AbstractDiscoverySource source;

	public AbstractDiscoverySource getSource() {
		return source;
	}

	public void setSource(AbstractDiscoverySource source) {
		this.source = source;
	}

}
