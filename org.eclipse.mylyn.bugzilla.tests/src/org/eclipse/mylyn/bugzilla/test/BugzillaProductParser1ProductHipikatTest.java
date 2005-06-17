/*******************************************************************************
 * Copyright (c) 2003 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.bugzilla.test;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Path;
import org.eclipse.mylar.bugzilla.core.internal.ProductParser;


/**
 * Tests for parsing Product Page for new Bugzilla reports
 */
public class BugzillaProductParser1ProductHipikatTest extends TestCase {

	public BugzillaProductParser1ProductHipikatTest() {
		super();
	}

	public BugzillaProductParser1ProductHipikatTest(String arg0) {
		super(arg0);
	}

	public void testOneProduct() throws Exception {

		// If only one product, should skip product page (nothing in product
		// list
		// and display the attributes for that product

		File f = FileTool.getFileInPlugin(BugzillaTestPlugin.getDefault(), new Path("TestPages/product-page-1-product-hipikat.html"));
		
		Reader in = new FileReader(f);

		List<String> productList = new ArrayList<String>();
		productList =  new ProductParser(in).getProducts();
//		printList(productList);

		assertNull("There were products parsed, and there shouldn't have been", productList);
	}

//	private void printList(List<String> productList) {
//
//		if(productList == null){
//			System.out.println("No products");
//			return;
//		}
//		Iterator<String> itr = productList.iterator();
//		System.out.println("Product List:");
//		if (!itr.hasNext())
//			System.out.println("No products");
//		while (itr.hasNext())
//			System.out.println(itr.next());
//	}
}