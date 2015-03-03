/*
 * (C) Copyright 2014 Kurento (http://kurento.org/)
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
package org.kurento.test.base;

import org.junit.Before;
import org.junit.experimental.categories.Category;
import org.kurento.commons.testing.SystemFunctionalTests;
import org.kurento.repository.Repository;
import org.kurento.repository.RepositoryApiConfiguration;
import org.kurento.repository.internal.http.RepositoryHttpServlet;
import org.kurento.test.client.KurentoTestClient;
import org.kurento.test.config.TestScenario;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.google.common.io.Files;

/**
 * Base for repository tests.
 * 
 * @author Micael Gallego (micael.gallego@gmail.com)
 * @author Boni Garcia (bgarcia@gsyc.es)
 * @since 5.0.4
 */
@Configuration
@ComponentScan(basePackages = { "org.kurento.repository" })
@EnableAutoConfiguration
@Category(SystemFunctionalTests.class)
public class RepositoryFunctionalTest extends KurentoClientTest {

	public Repository repository;

	public RepositoryFunctionalTest(TestScenario testScenario) {
		super(testScenario);
		this.setClient(new KurentoTestClient());
	}

	public RepositoryFunctionalTest() {
		super();
	}

	@Override
	public KurentoTestClient getBrowser() {
		return (KurentoTestClient) super.getBrowser();
	}

	@Override
	public KurentoTestClient getBrowser(String browserKey) {
		return (KurentoTestClient) super.getBrowser(browserKey);
	}

	@Override
	public KurentoTestClient getPresenter() {
		return (KurentoTestClient) super.getPresenter();
	}

	@Override
	public KurentoTestClient getViewer() {
		return (KurentoTestClient) super.getViewer();
	}

	@Override
	public KurentoTestClient getPresenter(int index) {
		return (KurentoTestClient) super.getPresenter(index);
	}

	@Override
	public KurentoTestClient getViewer(int index) {
		return (KurentoTestClient) super.getViewer(index);
	}

	@Bean
	public RepositoryHttpServlet repositoryHttpServlet() {
		return new RepositoryHttpServlet();
	}

	@Bean
	public ServletRegistrationBean repositoryServletRegistrationBean(
			RepositoryHttpServlet repositoryHttpServlet) {
		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(
				repositoryHttpServlet, "/repository_servlet/*");
		servletRegistrationBean.setLoadOnStartup(1);
		return servletRegistrationBean;
	}

	@Bean
	public RepositoryApiConfiguration repositoryApiConfiguration() {
		RepositoryApiConfiguration config = new RepositoryApiConfiguration();
		config.setWebappPublicURL("http://localhost:" + getServerPort() + "/");
		config.setFileSystemFolder(Files.createTempDir().toString());
		config.setRepositoryType("filesystem");
		return config;
	}

	@Before
	public void setupHttpServer() throws Exception {
		repository = (Repository) context.getBean("repository");
	}
}
