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

package com.google.gwtorm.protobuf;

import com.google.gwtorm.client.Column;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;

/**
 * Encode and decode an arbitrary Java object as a Protobuf message.
 * <p>
 * The object must use the {@link Column} annotations to denote the fields that
 * should be encoded or decoded.
 */
public abstract class ProtobufCodec<T> {
  /** Encode the object into an immutable byte string. */
  public abstract ByteString encode(T obj);

  /** Compute the number of bytes of the encoded form of the object. */
  public abstract int sizeof(T obj);

  /** Decode a byte string into an object instance. */
  public T decode(ByteString buf) {
    return decode(buf.newCodedInput());
  }

  /** Decode a byte string into an object instance. */
  public T decode(byte[] buf) {
    return decode(CodedInputStream.newInstance(buf));
  }

  /** Decode an object by reading it from the stream. */
  protected abstract T decode(CodedInputStream in);
}
