/*******************************************************************************
 * Copyright (c) 2003 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.bugzilla.core.internal;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylar.bugzilla.core.internal.HtmlStreamTokenizer.Token;
import org.eclipse.mylar.bugzilla.core.search.BugzillaQueryPageParser;
import org.eclipse.mylar.tasklist.repositories.TaskRepository;

/**
 * This class is used to parse the available products to log a bug for
 * 
 * @author Shawn Minto
 * @author Mik Kersten (hardening of initial prototype)
 */
public class ProductParser 
{
    /** Tokenizer used on the stream */
    private HtmlStreamTokenizer tokenizer;

    public ProductParser(Reader in) {
        tokenizer = new HtmlStreamTokenizer(in, null);
    }
    
	/**
	 * Parse the product page for the valid products that a bug can be logged for
	 * @param repository 
	 * @param in The input stream for the products page
	 * @return A list of the products that we can enter bugs for
	 * @throws IOException
	 * @throws ParseException
	 */
	public List<String> getProducts(TaskRepository repository) throws IOException, ParseException, LoginException
	{
		BugzillaQueryPageParser parser = new BugzillaQueryPageParser(repository, new NullProgressMonitor());
		if (!parser.wasSuccessful()) {
			throw new RuntimeException("Couldn't get products");
		} else {
			return Arrays.asList(parser.getProductValues());
		}
		
//		ArrayList<String> products = null;
//
//		boolean isTitle = false;
//		boolean possibleBadLogin = false;
//		String title = "";
//
//		for (HtmlStreamTokenizer.Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
//				
//			// make sure that bugzilla doesn't want us to login
//			if(token.getType() == Token.TAG && ((HtmlTag)(token.getValue())).getTagType() == HtmlTag.Type.TITLE && !((HtmlTag)(token.getValue())).isEndTag())
//			{
//				isTitle = true;
//				continue;
//			}
//			
//			if(isTitle)
//			{
//				// get all of the data in the title tag
//				if(token.getType() != Token.TAG)
//				{
//					title += ((StringBuffer)token.getValue()).toString().toLowerCase() + " ";
//					continue;
//				}
//				else if(token.getType() == Token.TAG && ((HtmlTag)token.getValue()).getTagType() == HtmlTag.Type.TITLE && ((HtmlTag)token.getValue()).isEndTag())
//				{
//					// compare the title to see if we think that there is a problem with login
//					if((title.indexOf("login") != -1 || (title.indexOf("invalid") != -1 && title.indexOf("password") != -1) ||  title.indexOf("check e-mail") != -1 || title.indexOf("error") != -1))
//						possibleBadLogin = true;
//					isTitle = false;
//					title = "";
//				}
//					continue;
//			}
//				
//			if (token.getType() == Token.TAG ) {
//				HtmlTag tag = (HtmlTag) token.getValue();
//				if (tag.getTagType() == HtmlTag.Type.TR) 
//				{
//					token = tokenizer.nextToken();
//					if(token.getType() != Token.EOF && token.getType() == Token.TAG)
//					{
//						tag = (HtmlTag)token.getValue();
//						if(tag.getTagType() != HtmlTag.Type.TH)
//							continue;
//						else
//						{
//							if(products == null)
//                                products = new ArrayList<String>();
//                            parseProducts(products);
//							
//						}
//					}
//					continue;
//				}
//			}
//		}
//		
//		// if we have no products and we suspect a login error, assume that it was a login error
//		if(products == null && possibleBadLogin)
//			throw new LoginException(IBugzillaConstants.MESSAGE_LOGIN_FAILURE);
//		return products;
	}

	/**
	 * Parse the products that we can enter bugs for
	 * @param products The list of products to add this new product to
	 * 
	 * TODO: remove, not used
	 */
	public  void parseProducts(List<String> products) throws IOException, ParseException 
	{
		StringBuffer sb = new StringBuffer();

		for (HtmlStreamTokenizer.Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) 
		{
			if(token.getType() == Token.TAG)
			{
				HtmlTag tag = (HtmlTag)token.getValue();
				if(tag.getTagType() == HtmlTag.Type.TH && tag.isEndTag())
					break;
			}
			else if(token.getType() == Token.TEXT)
				sb.append(token.toString());
		}
		
		String prod = HtmlStreamTokenizer.unescape(sb).toString();
		if(prod.endsWith(":"))
			prod = prod.substring(0, prod.length() - 1);
		products.add(prod);
				
		for (HtmlStreamTokenizer.Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) 
		{
			if(token.getType() == Token.TAG)
			{
				HtmlTag tag = (HtmlTag)token.getValue();
				if(tag.getTagType() == HtmlTag.Type.TR && tag.isEndTag())
					break;
		
			}
		}
	}
}
