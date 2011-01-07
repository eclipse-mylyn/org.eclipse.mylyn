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

package com.google.gwtjsonrpc.client.impl.ser;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwtjsonrpc.client.impl.JsonSerializer;
import com.google.gwtjsonrpc.client.impl.ResultDeserializer;

import java.util.Date;

/** Default serialization for a {@link java.util.Date}. */
public final class JavaUtilDate_JsonSerializer extends
    JsonSerializer<java.util.Date> implements
    ResultDeserializer<java.util.Date> {
  public static final JavaUtilDate_JsonSerializer INSTANCE =
      new JavaUtilDate_JsonSerializer();

  @Override
  public java.util.Date fromJson(final Object o) {
    if (o != null) {
      return parse((String) o);
    }
    return null;
  }

  @Override
  public void printJson(final StringBuilder sb, final java.util.Date o) {
    sb.append('"');
    sb.append(o);
    sb.append('"');
  }

  @SuppressWarnings("deprecation")
  private static Date parse(final String o) {
    return new java.util.Date(o);
  }

  @Override
  public Date fromResult(JavaScriptObject responseObject) {
    return fromJson(PrimitiveResultDeserializers.stringResult(responseObject));
  }
}
