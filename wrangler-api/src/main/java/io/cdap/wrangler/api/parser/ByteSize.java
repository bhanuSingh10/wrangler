/*
 * Copyright © 2025 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.cdap.wrangler.api.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class ByteSize implements Token {
    private final double value;
    private final String unit;

    public ByteSize(String tokenText) {
        int i = 0;
        while (i < tokenText.length() &&
              (Character.isDigit(tokenText.charAt(i)) || tokenText.charAt(i) == '.')) {
            i++;
        }
        this.value = Double.parseDouble(tokenText.substring(0, i));
        this.unit = tokenText.substring(i).toUpperCase(); // e.g., KB, MB
    }

    public long getBytes() {
        switch (unit) {
            case "KB": return (long) (value * 1024);
            case "MB": return (long) (value * 1024 * 1024);
            case "GB": return (long) (value * 1024 * 1024 * 1024);
            default: throw new IllegalArgumentException("Unknown byte unit: " + unit);
        }
    }

    @Override
    public Object value() {
        return getBytes(); // canonical value
    }

    @Override
    public TokenType type() {
        return TokenType.BYTE_SIZE;
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(value + unit); // e.g., "10KB"
    }
}
