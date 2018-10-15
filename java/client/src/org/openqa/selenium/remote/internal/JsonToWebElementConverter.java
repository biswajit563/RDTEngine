// Licensed to the Software Freedom Conservancy (SFC) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The SFC licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.openqa.selenium.remote.internal;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.Dialect;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;

import java.util.Collection;
import java.util.Map;

/**
 * Reconstitutes {@link WebElement}s from their JSON representation. Will recursively convert Lists
 * and Maps to catch nested references. All other values pass through the converter unchanged.
 */
public class JsonToWebElementConverter implements Function<Object, Object> {

  private final RemoteWebDriver driver;

  public JsonToWebElementConverter(RemoteWebDriver driver) {
    this.driver = driver;
  }

  public Object apply(Object result) {
    //System.out.println(result.toString());
    if (result instanceof Collection<?>) {
      Collection<?> results = (Collection<?>) result;
      return Lists.newArrayList(Iterables.transform(results, this));
    }

    if (result instanceof Map<?, ?>) {
      Map<?, ?> resultAsMap = (Map<?, ?>) result;
      String elementKey = getElementKey(resultAsMap);
		  if (null != elementKey) {
			  RemoteWebElement element = newRemoteWebElement();
			  System.out.println("Called new RemoteWebElement ");
			  element.setId(String.valueOf(resultAsMap.get(elementKey)));
			  return element;
      } else {
        return Maps.transformValues(resultAsMap, this);
      }
    }

    if (result instanceof RemoteWebElement) {
      System.out.println(" result is instance of remoteWebElement  ");
      return setOwner((RemoteWebElement) result);
    }

    if (result instanceof Number) {
      if (result instanceof Float || result instanceof Double) {
        System.out.println(" result is instance of Number Float Double ");
        return ((Number) result).doubleValue();
      }
      System.out.println(" result is instance of Number ");
      return ((Number) result).longValue();
    }

    return result;
  }

  protected RemoteWebElement newRemoteWebElement() {
    return setOwner(new RemoteWebElement());
  }

  private RemoteWebElement setOwner(RemoteWebElement element) {
    if (driver != null) {
      element.setParent(driver);
      element.setFileDetector(driver.getFileDetector());
    }
    return element;
  }
  private String getElementKey(Map<?, ?> resultAsMap) {
		for (Dialect d : Dialect.values()) {
			String elementKeyForDialect = d.getEncodedElementKey();
			if (resultAsMap.containsKey(elementKeyForDialect)) {
				return elementKeyForDialect;
			}
		}
		return null;
	}
}
