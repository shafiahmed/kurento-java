/*
 * (C) Copyright 2015 Kurento (http://kurento.org/)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */

package org.kurento.test.config;

import static org.kurento.commons.PropertiesManager.getProperty;
import static org.kurento.test.config.TestConfiguration.TEST_CONFIG_EXECUTIONS_DEFAULT;
import static org.kurento.test.config.TestConfiguration.TEST_CONFIG_EXECUTIONS_PROPERTY;

import java.io.BufferedReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Assert;
import org.kurento.commons.ClassPath;
import org.kurento.test.base.KurentoTest;
import org.kurento.test.browser.Browser;
import org.kurento.test.browser.BrowserType;
import org.kurento.test.browser.WebPageType;
import org.openqa.selenium.Platform;

import com.google.gson.Gson;

/**
 * Scenarios for test (e.g. one local browser and other in remote...)
 *
 * @author Boni Garcia (bgarcia@gsyc.es)
 * @since 5.1.0
 */
public class TestScenario {

  public final static String INSTANCES_SEPARATOR = "-";

  private Map<String, Browser> browserMap;
  private List<URL> urlList;

  public TestScenario() {
    browserMap = new TreeMap<>();
    urlList = new ArrayList<>();
  }

  public void addBrowser(String id, Browser browser) {
    if (browser.getNumInstances() > 0) {
      for (int i = 0; i < browser.getNumInstances(); i++) {
        if (browser.getBrowserPerInstance() > 1) {
          for (int j = 0; j < browser.getBrowserPerInstance(); j++) {
            String browserId = id + i + INSTANCES_SEPARATOR + j;
            addBrowserInstance(browserId, new Browser(browser.getBuilder()));
          }
        } else {
          addBrowserInstance(id + i, new Browser(browser.getBuilder()));
        }
      }
    } else {
      addBrowserInstance(id, browser);
    }
  }

  private void addBrowserInstance(String id, Browser browser) {
    assertKeyNotExist(id);
    browser.setId(id);
    browserMap.put(id, browser);
  }

  private void assertKeyNotExist(String key) {
    Assert.assertFalse("'" + key + "' key already registered in browser config map",
        browserMap.keySet().contains(key));
  }

  public BrowserScope getScope(String key) {
    return browserMap.get(key).getScope();
  }

  public BrowserType getBrowserType(String key) {
    return browserMap.get(key).getBrowserType();
  }

  public Platform getPlatform(String key) {
    return browserMap.get(key).getPlatform();
  }

  public String getBrowserVersion(String key) {
    return browserMap.get(key).getBrowserVersion();
  }

  @Override
  public String toString() {
    String out = "";
    Map<String, Integer> browsers = new HashMap<>();
    for (String key : browserMap.keySet()) {
      String browser = getBrowserType(key).toString();
      String version = getBrowserVersion(key);
      Platform platform = getPlatform(key);

      if (version != null) {
        browser += version;
      }
      if (platform != null) {
        browser += platform;
      }
      if (browsers.containsKey(browser)) {
        int newCount = browsers.get(browser) + 1;
        browsers.put(browser, newCount);
      } else {
        browsers.put(browser, 1);
      }
    }
    for (String browser : browsers.keySet()) {
      int count = browsers.get(browser);
      if (!out.isEmpty()) {
        out += " ";
      }
      if (count > 1) {
        out += count + "X";
      }
      out += browser;
    }
    return out;
  }

  public static Collection<Object[]> from(String defaultBrowserConfigFile) {

    try {

      // Load executions from config file or system properties
      String executionsData =
          getProperty(getProperty(TEST_CONFIG_EXECUTIONS_PROPERTY, TEST_CONFIG_EXECUTIONS_DEFAULT));

      BrowserConfig browserConfig = null;
      Gson gson = new Gson();
      if (executionsData != null) {
        browserConfig =
            gson.fromJson("{\"executions\":" + executionsData + "}", BrowserConfig.class);

      } else {

        // If there is no browserConfig in config file, load default
        // from defaultBrowserConfigFile
        try (BufferedReader br = Files.newBufferedReader(
            ClassPath.get("/" + defaultBrowserConfigFile), StandardCharsets.UTF_8)) {
          browserConfig = gson.fromJson(br, BrowserConfig.class);
        }
      }

      return browserConfig.getTestScenario();

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static Collection<Object[]> empty() {
    TestScenario test = new TestScenario();
    return Arrays.asList(new Object[][] { { test } });
  }

  /*
   * Local browsers
   */
  public static Collection<Object[]> localChromeAndFirefox() {
    // Test #1 : Chrome in local
    TestScenario test1 = new TestScenario();
    test1.addBrowser(BrowserConfig.BROWSER, new Browser.Builder().webPageType(WebPageType.WEBRTC)
        .browserType(BrowserType.CHROME).scope(BrowserScope.LOCAL).build());
    // Test #2 : Firefox in local
    TestScenario test2 = new TestScenario();
    test2.addBrowser(BrowserConfig.BROWSER, new Browser.Builder().webPageType(WebPageType.WEBRTC)
        .browserType(BrowserType.FIREFOX).scope(BrowserScope.LOCAL).build());

    return Arrays.asList(new Object[][] { { test1 }, { test2 } });
  }

  public static Collection<Object[]> localChromePlusFirefox() {
    // Test #1 : Firefox and Chrome in local
    TestScenario test = new TestScenario();
    test.addBrowser(BrowserConfig.BROWSER + 0, new Browser.Builder().webPageType(WebPageType.WEBRTC)
        .browserType(BrowserType.CHROME).scope(BrowserScope.LOCAL).build());
    test.addBrowser(BrowserConfig.BROWSER + 1, new Browser.Builder().webPageType(WebPageType.WEBRTC)
        .browserType(BrowserType.FIREFOX).scope(BrowserScope.LOCAL).build());

    return Arrays.asList(new Object[][] { { test } });
  }

  public static Collection<Object[]> localChromes(int size, WebPageType webPageType) {
    // Test: Chrome(s) in local
    TestScenario test = new TestScenario();
    for (int i = 0; i < size; i++) {
      test.addBrowser(BrowserConfig.BROWSER + i, new Browser.Builder().webPageType(webPageType)
          .browserType(BrowserType.CHROME).scope(BrowserScope.LOCAL).build());
    }
    return Arrays.asList(new Object[][] { { test } });
  }

  public static Collection<Object[]> localChromes(int size) {
    // Test: Chrome(s) in local
    return localChromes(size, WebPageType.WEBRTC);
  }

  public static Collection<Object[]> localChromesWithRgbVideo(int size) {
    // Test: Chrome(s) in local
    TestScenario test = new TestScenario();
    for (int i = 0; i < size; i++) {
      test.addBrowser(BrowserConfig.BROWSER + i,
          new Browser.Builder().webPageType(WebPageType.WEBRTC).browserType(BrowserType.CHROME)
              .video(KurentoTest.getTestFilesPath() + "/video/15sec/rgbHD.y4m")
              .scope(BrowserScope.LOCAL).build());
    }
    return Arrays.asList(new Object[][] { { test } });
  }

  public static Collection<Object[]> localChromesAndFirefoxs(int size) {
    // Test #1 : Chrome's in local
    TestScenario test1 = new TestScenario();
    for (int i = 0; i < size; i++) {
      test1.addBrowser(BrowserConfig.BROWSER + i,
          new Browser.Builder().webPageType(WebPageType.WEBRTC).browserType(BrowserType.CHROME)
              .scope(BrowserScope.LOCAL).build());
    }
    // Test #2 : Firefox's in local
    TestScenario test2 = new TestScenario();
    for (int i = 0; i < size; i++) {
      test2.addBrowser(BrowserConfig.BROWSER + i,
          new Browser.Builder().webPageType(WebPageType.WEBRTC).browserType(BrowserType.FIREFOX)
              .scope(BrowserScope.LOCAL).build());
    }

    return Arrays.asList(new Object[][] { { test1 }, { test2 } });
  }

  public static Collection<Object[]> localChrome(WebPageType webPageType) {
    // Test: Chrome in local
    TestScenario test = new TestScenario();
    test.addBrowser(BrowserConfig.BROWSER, new Browser.Builder().browserType(BrowserType.CHROME)
        .scope(BrowserScope.LOCAL).webPageType(webPageType).build());

    return Arrays.asList(new Object[][] { { test } });
  }

  public static Collection<Object[]> localChrome() {
    // Test: Chrome in local
    return localChrome(WebPageType.WEBRTC);
  }

  public static Collection<Object[]> localFirefox() {
    // Test: Firefox in local
    TestScenario test = new TestScenario();
    test.addBrowser(BrowserConfig.BROWSER, new Browser.Builder().webPageType(WebPageType.WEBRTC)
        .browserType(BrowserType.FIREFOX).scope(BrowserScope.LOCAL).build());

    return Arrays.asList(new Object[][] { { test } });
  }

  public static Collection<Object[]> localPresenterAndViewer() {
    // Test: Chrome in local (presenter and viewer)
    TestScenario test = new TestScenario();
    test.addBrowser(BrowserConfig.PRESENTER, new Browser.Builder().webPageType(WebPageType.WEBRTC)
        .browserType(BrowserType.CHROME).scope(BrowserScope.LOCAL).build());
    test.addBrowser(BrowserConfig.VIEWER, new Browser.Builder().webPageType(WebPageType.WEBRTC)
        .browserType(BrowserType.CHROME).scope(BrowserScope.LOCAL).build());

    return Arrays.asList(new Object[][] { { test } });
  }

  public static Collection<Object[]> localPresenterAndViewerRGB() {
    // Test: Chrome in local (presenter and viewer)
    String videoPath = KurentoTest.getTestFilesPath() + "/video/15sec/rgbHD.y4m";
    TestScenario test = new TestScenario();
    test.addBrowser(BrowserConfig.PRESENTER, new Browser.Builder().webPageType(WebPageType.WEBRTC)
        .browserType(BrowserType.CHROME).scope(BrowserScope.LOCAL).video(videoPath).build());
    test.addBrowser(BrowserConfig.VIEWER, new Browser.Builder().webPageType(WebPageType.WEBRTC)
        .browserType(BrowserType.CHROME).scope(BrowserScope.LOCAL).video(videoPath).build());

    return Arrays.asList(new Object[][] { { test } });
  }

  public Map<String, Browser> getBrowserMap() {
    return browserMap;
  }

  public Map<String, Browser> getBrowserMap(String... types) {
    Map<String, Browser> out = new HashMap<String, Browser>();
    for (String key : browserMap.keySet()) {
      for (String type : types) {
        if (key.startsWith(type)) {
          out.put(key, browserMap.get(key));
        }
      }
    }
    return out;
  }

  public List<URL> getUrlList() {
    return urlList;
  }

}
