/*********************************************************************
 * Copyright (c) 2010 Sony Ericsson/ST Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *********************************************************************/

package org.eclipse.gerrit.proxy;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.eclipse.gerrit.GerritHTTPClientError;

/**
 * Abstract class that handles the http communications with the Gerrit
 * server.
 * 
 * @author Daniel Olsson, ST Ericsson
 * @author Tomas Westling, Sony Ericsson -
 *         thomas.westling@sonyericsson.com
 */
public abstract class AbstractGerritHttpClient {

  private String schema; // http, https

  private String host; // server adress

  private int port;

  private Cookie xsrfKey;

  private int id = 1;

private final String path;

  /**
   * Constructor.
   * 
   * @param schema
   *          The schema to use i.e http or https
   * @param host
   *          The server address to the Gerrit host
 * @param path 
   * @param port
   *          The port that the communication should be relayed over
   */
  public AbstractGerritHttpClient(String schema, String host, String path, int port) {
    this.schema = schema;
    this.host = host;
	this.path = path;
    this.port = port;
  }

  /**
   * Send a JSON request to the Gerrit server.
   * 
   * @return The JSON response
   * @throws GerritHTTPClientError
   */
  public String postJsonRequest(String serviceUri, String message) throws GerritHTTPClientError {

    // Create a method instance
    PostMethod postMethod = new PostMethod(getURL() + serviceUri);
    postMethod.setRequestHeader("Content-Type", "application/json; charset=utf-8");
    postMethod.setRequestHeader("Accept", "application/json");

    try {
      RequestEntity requestEntity = new StringRequestEntity(message.toString(), "application/json", null);
      postMethod.setRequestEntity(requestEntity);

      // Execute the method.
      int statusCode = getHttpClient().executeMethod(postMethod);

      if (statusCode != HttpStatus.SC_OK) {
        System.err.println("Method failed: " + postMethod.getStatusLine() + "\n" + postMethod.getResponseBodyAsString());
        throw new GerritHTTPClientError();
      }

      // Release the connection.
      String retString = postMethod.getResponseBodyAsString();
      return retString;

    } catch (UnsupportedEncodingException e1) {
      e1.printStackTrace();
      throw new GerritHTTPClientError();
    } catch (HttpException e) {
      System.err.println("Fatal protocol violation: " + e.getMessage());
      e.printStackTrace();
      throw new GerritHTTPClientError();
    } catch (IOException e) {
      System.err.println("Fatal transport error: " + e.getMessage());
      e.printStackTrace();
      throw new GerritHTTPClientError();
    } finally {
      postMethod.releaseConnection();
    }
  }

  /**
   * Updates the Xsrf key which is needed in all methods where login
   * to the Gerrit server is needed.
   * 
   * @throws GerritHTTPClientError
   *           if either the connection fails or an error message from
   *           the server is received.
   */
  private void updateXsrfKey() throws GerritHTTPClientError {
    HttpClient client = getHttpClient();
    GetMethod getMethod = new GetMethod(getURL() + "/#mine");
    try {
      // Execute the method.
      // The code below where we first connect to /#mine, release it
      // and then connect to /login/mine
      // is needed for the connection to our internal Gerrit server
      // and will probably not work
      // towards review.source.android.com.
      int statusCode = client.executeMethod(getMethod);
      getMethod.releaseConnection();
      getMethod = new GetMethod(getURL() + "/login/mine");

      statusCode = client.executeMethod(getMethod);
      Cookie[] cookies = client.getState().getCookies();
      for (Cookie c : cookies) {
        if (c.getName().equals("GerritAccount")) {
          xsrfKey = c;
          break;
        }
      }
      if (statusCode != HttpStatus.SC_OK) {
        System.err.println("Method failed: " + getMethod.getStatusLine() + "\n" + getMethod.getResponseBodyAsString());
        throw new GerritHTTPClientError();
      }

    } catch (UnsupportedEncodingException e1) {
      e1.printStackTrace();
      throw new GerritHTTPClientError();
    } catch (HttpException e) {
      System.err.println("Fatal protocol violation: " + e.getMessage());
      e.printStackTrace();
      throw new GerritHTTPClientError();
    } catch (IOException e) {
      System.err.println("Fatal transport error: " + e.getMessage());
      e.printStackTrace();
      throw new GerritHTTPClientError();
    } finally {
      // Release the connection.
      getMethod.releaseConnection();
    }
  }

  /**
   * Fetch the xsrfKey which is a required parameter for most
   * requests.
   * 
   * @return the XsrfKey
   * @throws GerritHTTPClientError
   */
  public synchronized String getXsrfKey() throws GerritHTTPClientError {
    if (xsrfKey == null || xsrfKey.isExpired()) {
      updateXsrfKey();
    }
    return xsrfKey.getValue();
  }

  public abstract HttpClient getHttpClient() throws GerritHTTPClientError;

  public String getSchema() {
    return schema;
  }

  public void setSchema(String schema) {
    this.schema = schema;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getURL() {
    return schema + "://" + host + ":" + port + path;
  }

  public int getId() {
    return id++;
  }
}
