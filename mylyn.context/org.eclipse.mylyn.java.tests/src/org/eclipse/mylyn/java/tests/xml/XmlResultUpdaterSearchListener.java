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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.internal.resources.File;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.sdk.util.search.TestActiveSearchListener;
import org.eclipse.mylyn.internal.context.core.AbstractRelationProvider;
import org.eclipse.mylyn.internal.ide.ui.XmlNodeHelper;
import org.eclipse.mylyn.internal.pde.ui.PdeStructureBridge;
import org.eclipse.mylyn.internal.pde.ui.XmlJavaRelationProvider;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.search.internal.ui.text.FileSearchResult;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.part.FileEditorInput;

/**
 * @author Mik Kersten
 */
public class XmlResultUpdaterSearchListener extends TestActiveSearchListener {

	private List<IInteractionElement> results = null;

	private final IInteractionElement node;

	private final int degreeOfSeparation;

	public XmlResultUpdaterSearchListener(AbstractRelationProvider prov, IInteractionElement searchNode,
			int degreeOfSeparation) {
		super(prov);
		node = searchNode;
		this.degreeOfSeparation = degreeOfSeparation;
	}

	private boolean gathered = false;

	@Override
	public void searchCompleted(List<?> l) {
		results = new ArrayList<>();

		if (l.isEmpty()) {
			return;
		}

		Map<String, String> nodes = new HashMap<>();

		if (l.get(0) instanceof FileSearchResult) {
			FileSearchResult fsr = (FileSearchResult) l.get(0);

			Object[] far = fsr.getElements();
			for (Object element : far) {
				Match[] mar = fsr.getMatches(element);

				if (element instanceof File f) {
					// change the file into a document
					FileEditorInput fei = new FileEditorInput(f);

					for (Match m : mar) {
						try {
							XmlNodeHelper xnode = new XmlNodeHelper(fei.getFile().getFullPath().toString(),
									m.getOffset());
							AbstractContextStructureBridge bridge = ContextCore.getStructureBridge(f.getName());
							String handle = xnode.getHandle();
							Object o = bridge.getObjectForHandle(handle);
							String name = bridge.getLabel(o);
							if (o != null) {
								nodes.put(handle, name);
								results.add(node);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		for (String handle : nodes.keySet()) {
			incrementInterest(node, PdeStructureBridge.CONTENT_TYPE, handle, degreeOfSeparation);
		}
		gathered = true;
	}

	protected void incrementInterest(IInteractionElement node, String elementKind, String elementHandle,
			int degreeOfSeparation) {
		int predictedInterest = 1;// (7-degreeOfSeparation) *
		// TaskscapeManager.getScalingFactors().getDegreeOfSeparationScale();
		InteractionEvent event = new InteractionEvent(InteractionEvent.Kind.PREDICTION, elementKind, elementHandle,
				XmlJavaRelationProvider.SOURCE_ID, XmlJavaRelationProvider.SOURCE_ID, null, predictedInterest);
		ContextCore.getContextManager().processInteractionEvent(event);

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
