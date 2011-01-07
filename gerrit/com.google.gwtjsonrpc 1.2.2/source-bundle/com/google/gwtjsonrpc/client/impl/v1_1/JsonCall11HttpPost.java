// Copyright 2009 Google Inc.
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

package com.google.gwtjsonrpc.client.impl.v1_1;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.InvocationException;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwtjsonrpc.client.JsonUtil;
import com.google.gwtjsonrpc.client.RemoteJsonException;
import com.google.gwtjsonrpc.client.event.RpcCompleteEvent;
import com.google.gwtjsonrpc.client.impl.AbstractJsonProxy;
import com.google.gwtjsonrpc.client.impl.JsonCall;
import com.google.gwtjsonrpc.client.impl.ResultDeserializer;

/** JsonCall implementation for JsonRPC version 1.1 over HTTP POST */
public class JsonCall11HttpPost<T> extends JsonCall<T> {

  public JsonCall11HttpPost(AbstractJsonProxy abstractJsonProxy,
      String methodName, String requestParams,
      ResultDeserializer<T> resultDeserializer, AsyncCallback<T> callback) {
    super(abstractJsonProxy, methodName, requestParams, resultDeserializer,
        callback);
  }

  @Override
  protected void send() {
    final StringBuilder body = new StringBuilder();
    body.append("{\"version\":\"1.1\",\"method\":\"");
    body.append(methodName);
    body.append("\",\"params\":");
    body.append(requestParams);
    final String xsrfKey = proxy.getXsrfManager().getToken(proxy);
    if (xsrfKey != null) {
      body.append(",\"xsrfKey\":");
      body.append(JsonUtils.escapeValue(xsrfKey));
    }
    body.append("}");

    final RequestBuilder rb;
    rb = new RequestBuilder(RequestBuilder.POST, proxy.getServiceEntryPoint());
    rb.setHeader("Content-Type", JsonUtil.JSON_REQ_CT);
    rb.setHeader("Accept", JsonUtil.JSON_TYPE);
    rb.setCallback(this);
    rb.setRequestData(body.toString());

    send(rb);
  }

  @Override
  public void onResponseReceived(final Request req, final Response rsp) {
    final int sc = rsp.getStatusCode();
    if (isJsonBody(rsp)) {
      final RpcResult r;
      try {
        r = parse(jsonParser, rsp.getText());
      } catch (RuntimeException e) {
        RpcCompleteEvent.fire(this);
        callback.onFailure(new InvocationException("Bad JSON response: " + e));
        return;
      }

      if (r.xsrfKey() != null) {
        proxy.getXsrfManager().setToken(proxy, r.xsrfKey());
      }

      if (r.error() != null) {
        final String errmsg = r.error().message();
        if (JsonUtil.ERROR_INVALID_XSRF.equals(errmsg)) {
          if (attempts < 2) {
            // The XSRF cookie was invalidated (or didn't exist) and the
            // service demands we have one in place to make calls to it.
            // A new token was returned to us, so start the request over.
            //
            send();
          } else {
            RpcCompleteEvent.fire(this);
            callback.onFailure(new InvocationException(errmsg));
          }
        } else {
          RpcCompleteEvent.fire(this);
          callback.onFailure(new RemoteJsonException(errmsg, r.error().code(),
              new JSONObject(r.error()).get("error")));
        }
        return;
      }

      if (sc == Response.SC_OK) {
        RpcCompleteEvent.fire(this);
        JsonUtil.invoke(resultDeserializer, callback, r);
        return;
      }
    }

    if (sc == Response.SC_OK) {
      RpcCompleteEvent.fire(this);
      callback.onFailure(new InvocationException("No JSON response"));
    } else {
      RpcCompleteEvent.fire(this);
      callback.onFailure(new StatusCodeException(sc, rsp.getStatusText()));
    }
  }

  private static boolean isJsonBody(final Response rsp) {
    String type = rsp.getHeader("Content-Type");
    if (type == null) {
      return false;
    }
    int semi = type.indexOf(';');
    if (semi >= 0) {
      type = type.substring(0, semi).trim();
    }
    return JsonUtil.JSON_TYPE.equals(type);
  }

  /**
   * Call a JSON parser javascript function to parse an encoded JSON string.
   * 
   * @param parser a javascript function
   * @param json encoded JSON text
   * @return the parsed data
   * @see #jsonParser
   */
  private static final native RpcResult parse(JavaScriptObject parserFunction,
      String json)
  /*-{
    return parserFunction(json);
  }-*/;


  private static class RpcResult extends JavaScriptObject {
    protected RpcResult() {
    }

    final native RpcError error()/*-{ return this.error; }-*/;

    final native String xsrfKey()/*-{ return this.xsrfKey; }-*/;
  }

  private static class RpcError extends JavaScriptObject {
    protected RpcError() {
    }

    final native String message()/*-{ return this.message; }-*/;

    final native int code()/*-{ return this.code; }-*/;
  }
}
