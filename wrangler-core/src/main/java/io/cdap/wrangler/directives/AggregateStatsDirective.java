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
package io.cdap.wrangler.directives;

import io.cdap.wrangler.api.*;
import io.cdap.wrangler.api.parser.ByteSize;
import io.cdap.wrangler.api.parser.TimeDuration;
import io.cdap.wrangler.api.parser.TokenType;
import io.cdap.wrangler.api.parser.ColumnName;
import io.cdap.wrangler.api.parser.UsageDefinition;

import java.util.Collections;
import java.util.List;

public class AggregateStatsDirective implements Directive {
    private String sourceSizeColumn;
    private String sourceTimeColumn;
    private String targetSizeColumn;
    private String targetTimeColumn;

    @Override
    
       public UsageDefinition define() {
    UsageDefinition.Builder builder = UsageDefinition.builder("aggregate-stats");
    builder.define("size", TokenType.COLUMN_NAME);
    builder.define("time", TokenType.COLUMN_NAME);
    builder.define("total_size", TokenType.COLUMN_NAME);
    builder.define("total_time", TokenType.COLUMN_NAME);
    return builder.build();


    }

    @Override
    public void initialize(Arguments args) {
        this.sourceSizeColumn = ((ColumnName) args.value("size")).value();
        this.sourceTimeColumn = ((ColumnName) args.value("time")).value();
        this.targetSizeColumn = ((ColumnName) args.value("total_size")).value();
        this.targetTimeColumn = ((ColumnName) args.value("total_time")).value();
    }

    @Override
    public List<Row> execute(List<Row> rows, ExecutorContext context) throws DirectiveExecutionException {
        long totalBytes = 0;
        long totalTimeMs = 0;

        for (Row row : rows) {
            Object sizeObj = row.getValue(sourceSizeColumn);
            Object timeObj = row.getValue(sourceTimeColumn);

            if (!(sizeObj instanceof ByteSize)) {
                throw new DirectiveExecutionException("Expected ByteSize in column: " + sourceSizeColumn);
            }

            if (!(timeObj instanceof TimeDuration)) {
                throw new DirectiveExecutionException("Expected TimeDuration in column: " + sourceTimeColumn);
            }

            totalBytes += ((ByteSize) sizeObj).getBytes();
            totalTimeMs += ((TimeDuration) timeObj).getCanonicalTime();
        }

        double totalSizeMB = totalBytes / (1024.0 * 1024.0);
        double totalTimeSec = totalTimeMs / 1000.0;

        Row output = new Row();
        output.addOrSet(targetSizeColumn, totalSizeMB);
        output.addOrSet(targetTimeColumn, totalTimeSec);

        return Collections.singletonList(output);
    }

    @Override
    public void destroy() {
        // No cleanup needed
    }
}
