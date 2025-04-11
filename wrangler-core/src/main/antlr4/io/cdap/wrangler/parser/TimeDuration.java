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

public class TimeDuration extends Token {
    private final double value;
    private final String unit;

    public TimeDuration(String tokenText) {
        // tokenText example: "150ms" or "2.1s"
        int len = tokenText.length();
        int index = 0;
        while (index < len && (Character.isDigit(tokenText.charAt(index)) || tokenText.charAt(index) == '.')) {
            index++;
        }
        this.value = Double.parseDouble(tokenText.substring(0, index));
        this.unit = tokenText.substring(index).toLowerCase();
    }

    public long getMilliseconds() {
        switch (unit) {
            case "ms":
                return (long) value;
            case "s":
                return (long) (value * 1000);
            default:
                throw new IllegalArgumentException("Unknown time unit: " + unit);
        }
    }
}
