package org.eclipse.mylyn.versions.tasks.mapper.internal;
/*******************************************************************************
 * Copyright (c) 2012 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Research Group for Industrial Software (INSO), Vienna University of Technology - initial API and implementation
 *******************************************************************************/

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

/**
 * 
 * @author Kilian Matt
 *
 */
public class IndexedFieldsTest {

	@Test
	public void indexKeyIsUnique(){
		Set<String > fields = new TreeSet<String>();
		for(IndexedFields f : IndexedFields.values()){
			String indexKey = f.getIndexKey();
			assertNotNull(indexKey);
			if(fields.contains(indexKey)){
				fail("Duplicate Index key " + indexKey);
			}	
			fields.add(indexKey);
		}
	}
	
}
