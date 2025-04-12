/*
 * Copyright © 2017-2019 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.cdap.wrangler.parser;

import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.parser.ByteSize;
import io.cdap.wrangler.api.parser.TimeDuration;
import io.cdap.wrangler.api.parser.ColumnName;
import io.cdap.wrangler.api.Arguments;
import io.cdap.directives.parser.DummyExecutorContext;
import io.cdap.directives.parser.DummyArguments;
import io.cdap.wrangler.directives.AggregateStatsDirective;
import io.cdap.wrangler.api.parser.ColumnName;
import io.cdap.wrangler.api.parser.Token;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;
/**
 * Unit test for {@link AggregateStatsDirective}.
 */

public class AggregateStatsDirectiveTest {

  @Test
  public void testAggregateStatsDirective() throws Exception {
    // Sample input rows
    List<Row> rows = new ArrayList<>();

    Row row1 = new Row();
    row1.add("data_transfer_size", new ByteSize("10KB"));
    row1.add("response_time", new TimeDuration("150ms"));

    Row row2 = new Row();
    row2.add("data_transfer_size", new ByteSize("20KB"));
    row2.add("response_time", new TimeDuration("200ms"));

    rows.add(row1);
    rows.add(row2);

    // Set up arguments
    

    Map<String, Token> argsMap = new HashMap<>();
argsMap.put("size", new ColumnName("data_transfer_size"));
argsMap.put("time", new ColumnName("response_time"));
argsMap.put("total_size", new ColumnName("total_size_mb"));
argsMap.put("total_time", new ColumnName("total_time_sec"));
Arguments args = new DummyArguments(argsMap);

    
    

    // Run directive
    AggregateStatsDirective directive = new AggregateStatsDirective();
    directive.initialize(args);
    List<Row> result = directive.execute(rows, new DummyExecutorContext());

    // Validate result
    Assert.assertEquals(1, result.size());
    Row output = result.get(0);

    double expectedTotalSizeMB = (30 * 1024) / (1024.0 * 1024.0); // 30KB to MB
    double expectedTotalTimeSec = 350 / 1000.0; // 150+200ms to sec

    Assert.assertEquals(expectedTotalSizeMB, (Double) output.getValue("total_size_mb"), 0.001);
    Assert.assertEquals(expectedTotalTimeSec, (Double) output.getValue("total_time_sec"), 0.001);
  }
}
