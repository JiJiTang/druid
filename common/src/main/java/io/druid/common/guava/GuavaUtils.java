/*
 * Druid - a distributed column store.
 * Copyright 2012 - 2015 Metamarkets Group Inc.
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

package io.druid.common.guava;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 */
public class GuavaUtils
{
  public static Function<String, String> formatFunction(final String formatString)
  {
    return new Function<String, String>()
    {
      @Override
      public String apply(@Nullable String input)
      {
        return String.format(formatString, input);
      }
    };
  }

  public static InputSupplier<BufferedReader> joinFiles(final File... files)
  {
    return joinFiles(Arrays.asList(files));
  }

  public static InputSupplier<BufferedReader> joinFiles(final List<File> files)
  {

    return new InputSupplier<BufferedReader>()
    {
      @Override
      public BufferedReader getInput() throws IOException
      {
        return new BufferedReader(
            CharStreams.join(
                Iterables.transform(
                    files,
                    new Function<File, InputSupplier<InputStreamReader>>()
                    {
                      @Override
                      public InputSupplier<InputStreamReader> apply(final File input)
                      {
                        return new InputSupplier<InputStreamReader>()
                        {
                          @Override
                          public InputStreamReader getInput() throws IOException
                          {
                            InputStream baseStream = new FileInputStream(input);
                            if (input.getName().endsWith(".gz")) {
                              baseStream = new GZIPInputStream(baseStream);
                            }

                            return new InputStreamReader(baseStream, Charsets.UTF_8);
                          }
                        };
                      }
                    }
                )
            ).getInput()
        );
      }
    };
  }
}
