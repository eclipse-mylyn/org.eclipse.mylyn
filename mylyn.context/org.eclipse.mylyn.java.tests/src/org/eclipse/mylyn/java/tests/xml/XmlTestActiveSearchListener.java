/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.java.tests.xml;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.internal.resources.File;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.sdk.util.search.TestActiveSearchListener;
import org.eclipse.mylyn.internal.context.core.AbstractRelationProvider;
import org.eclipse.mylyn.internal.ide.ui.XmlNodeHelper;
import org.eclipse.search.internal.ui.text.FileSearchResult;
import org.eclipse.search.ui.text.Match;

/**
 * @author Shawn Minto
 */
public class XmlTestActiveSearchListener extends TestActiveSearchListener {

	private List<?> results = null;

	public XmlTestActiveSearchListener(AbstractRelationProvider prov) {
		super(prov);
	}

	private boolean gathered = false;

	@Override
	public void searchCompleted(List<?> l) {

		results = l;

		// deal with File
		if (l.isEmpty()) {
			gathered = true;
			return;
		}

		if (l.get(0) instanceof FileSearchResult) {
			FileSearchResult fsr = (FileSearchResult) l.get(0);
			List<Object> nodes = new ArrayList<>();
			Object[] far = fsr.getElements();
			for (Object element : far) {
				Match[] mar = fsr.getMatches(element);

				if (element instanceof File f) {
					for (Match m : mar) {
						try {

							AbstractContextStructureBridge bridge = ContextCore.getStructureBridge(f.getName());

							String handle = bridge.getHandleForOffsetInObject(f, m.getOffset());

							XmlNodeHelper node = new XmlNodeHelper(handle);
							nodes.add(node);
						} catch (Exception e) {
							e.printStackTrace();
							// don't care
						}
					}
				}
			}
			results = nodes;
		}
		gathered = true;
	}

	@Override
	public boolean resultsGathered() {
		return gathered;
	}

	@Override
	public List<?> getResults() {
		return results;
	}

}
