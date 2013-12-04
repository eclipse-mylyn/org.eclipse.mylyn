package org.eclipse.mylyn.internal.wikitext.tests;
import org.eclipse.mylyn.wikitext.core.osgi.OsgiServiceLocator;
import org.eclipse.mylyn.wikitext.core.util.ServiceLocator;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html Contributors: David Green - initial API and implementation
 *******************************************************************************/

public class WikiTextTestsPlugin extends AbstractUIPlugin {

	public WikiTextTestsPlugin() {
		ServiceLocator.setImplementation(OsgiServiceLocator.class);
	}

}
