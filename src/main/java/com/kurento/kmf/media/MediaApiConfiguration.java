/*
 * (C) Copyright 2013 Kurento (http://kurento.org/)
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
package com.kurento.kmf.media;


/**
 * Configuration parameters for Media API. This class is intended to be created
 * as a bean inside an Spring context, and is needed by the Media API to work
 * correctly.
 * 
 * @author Luis López (llopez@gsyc.es)
 * @author Iván Gracia (igracia@gsyc.es)
 * @version 2.0.0
 */
public class MediaApiConfiguration {

	/**
	 * Address of the local thrift server, which will be used to receive events
	 * and error notifications sent by the Kurento Media Server.
	 */
	private String handlerAddress = "localhost";

	/**
	 * Port where the local thrift server will be listening.
	 */
	private int handlerPort = 9292;

	/**
	 * Minimal size of the thread pool serving requests from the thrift server.
	 * These threads will be kept in the pool, even if they are idle.
	 */
	private final int poolCoreSize = 10;

	/**
	 * Maximum number of threads to allow in the thread pool.
	 */
	private final int poolMaxSize = 100;

	/**
	 * Timeout (in milliseconds) that a Hanlder callback thread will wait before
	 * cancelling the request and throwing and exception.
	 */
	private final long poolExecutionTimeout = 50000L;

	/**
	 * Size of the queue used for holding tasks before they are executed in the
	 * thread pool.
	 */
	private final int poolMaxQueueSize = 100;

	/**
	 * Size of the pool of thrift clients. Each pool created by the abstract
	 * pool will be instantiated with this number of clients.
	 */
	private final int clientPoolSize = 5;

	/**
	 * Gets the address of the local thrift server, which will be used to
	 * receive events and error notifications sent by the Kurento Media Server.
	 * 
	 * @return The handler address.
	 */
	public String getHandlerAddress() {
		return handlerAddress;
	}

	/**
	 * Sets the address of the local thrift server, which will be used to
	 * receive events and error notifications sent by the Kurento Media Server.
	 * 
	 * @param handlerAddress
	 *            The address.
	 */
	public void setHandlerAddress(String handlerAddress) {
		this.handlerAddress = handlerAddress;
	}

	/**
	 * Gets the port of the local thrift server, which will be used to receive
	 * events and error notifications sent by the Kurento Media Server.
	 * 
	 * @return The local thrift server port.
	 */
	public int getHandlerPort() {
		return handlerPort;
	}

	/**
	 * Sets the port of the local thrift server, which will be used to receive
	 * events and error notifications sent by the Kurento Media Server.
	 * 
	 * @param handlerPort
	 *            The local thrift server port.
	 */
	public void setHandlerPort(int handlerPort) {
		this.handlerPort = handlerPort;
	}

}
