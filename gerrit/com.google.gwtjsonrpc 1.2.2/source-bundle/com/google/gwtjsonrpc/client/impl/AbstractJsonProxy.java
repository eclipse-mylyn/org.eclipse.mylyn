// Copyright 2008 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.gwtjsonrpc.client.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.InvocationException;
import com.google.gwt.user.client.rpc.RpcRequestBuilder;
import com.google.gwtjsonrpc.client.JsonDefTarget;
import com.google.gwtjsonrpc.client.JsonUtil;
import com.google.gwtjsonrpc.client.XsrfManager;

/**
 * Base class for generated {@link RemoteJsonService} implementations.
 * <p>
 * At runtime <code>GWT.create(Foo.class)</code> returns a subclass of this
 * class, implementing the Foo and {@link JsonDefTarget} interfaces.
 */
public abstract class AbstractJsonProxy implements JsonDefTarget {
  /** URL of the service implementation. */
  String url;

  private XsrfManager xsrfManager = JsonUtil.getDefaultXsrfManager();

  public String getServiceEntryPoint() {
    return url;
  }

  public void setServiceEntryPoint(final String address) {
    url = address;
  }

  @Override
  public XsrfManager getXsrfManager() {
    return xsrfManager;
  }

  @Override
  public void setXsrfManager(final XsrfManager m) {
    assert m != null;
    xsrfManager = m;
  }

  @Override
  public void setRpcRequestBuilder(RpcRequestBuilder builder) {
    if (builder != null)
      throw new UnsupportedOperationException(
          "A RemoteJsonService does not use the RpcRequestBuilder, so this method is unsupported.");
    /**
     * From the gwt docs:
     * 
     * Calling this method with a null value will reset any custom behavior to
     * the default implementation.
     * 
     * If builder == null, we just ignore this invocation.
     */
  }

  protected <T> void doInvoke(final String methodName, final String reqData,
      final ResultDeserializer<T> ser, final AsyncCallback<T> cb)
      throws InvocationException {
    if (url == null) {
      throw new NoServiceEntryPointSpecifiedException();
    }
    newJsonCall(this, methodName, reqData, ser, cb).send();
  }

  protected abstract <T> JsonCall<T> newJsonCall(AbstractJsonProxy proxy,
      final String methodName, final String reqData,
      final ResultDeserializer<T> ser, final AsyncCallback<T> cb);

  protected static native JavaScriptObject hostPageCacheGetOnce(String name)
  /*-{ var r = $wnd[name];$wnd[name] = null;return r ? {result: r} : null; }-*/;

  protected static native JavaScriptObject hostPageCacheGetMany(String name)
  /*-{ return $wnd[name] ? {result : $wnd[name]} : null; }-*/;
}
