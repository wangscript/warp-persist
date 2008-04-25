/**
 * Copyright (C) 2008 Wideplay Interactive.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wideplay.warp.db4o;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import static org.easymock.EasyMock.*;
import org.testng.annotations.Test;

import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.ext.ExtObjectContainer;
import com.wideplay.warp.persist.UnitOfWork;

/**
 * 
 * @author Jeffrey Chung (jeffreymchung@gmail.com)
 */
@Test(suiteName = "db4o")
public class Db4oSPRFilterTest {

	@Test
	public final void testDoFilter() throws IOException, ServletException {
		SessionPerRequestFilter filter = new SessionPerRequestFilter();
		Db4oLocalTxnInterceptor.setUnitOfWork(UnitOfWork.REQUEST);

		ObjectServer osMock = createMock(ObjectServer.class);
		ObjectContainer ocMock = createMock(ObjectContainer.class);
		ExtObjectContainer eocMock = createMock(ExtObjectContainer.class);
		new ObjectServerHolder().setObjectServer(osMock);

		FilterChain mockFilterChain = createMock(FilterChain.class);
		ServletRequest mockRequest = createMock(ServletRequest.class);
		ServletResponse mockResponse = createMock(ServletResponse.class);

		mockFilterChain.doFilter(mockRequest, mockResponse);
		expect(osMock.openClient()).andReturn(ocMock);
		expect(ocMock.ext()).andReturn(eocMock);
		expect(eocMock.isClosed()).andReturn(false);
		expect(ocMock.close()).andReturn(true);

		replay(mockFilterChain, mockRequest, mockResponse, osMock, ocMock, eocMock);

		filter.doFilter(mockRequest, mockResponse, mockFilterChain);

		verify(mockFilterChain, mockRequest, mockResponse, osMock, ocMock, eocMock);
	}

	@Test
	public final void testDoFilterWithException() throws IOException, ServletException {
		SessionPerRequestFilter filter = new SessionPerRequestFilter();
		Db4oLocalTxnInterceptor.setUnitOfWork(UnitOfWork.REQUEST);

		ObjectServer osMock = createMock(ObjectServer.class);
		ObjectContainer ocMock = createMock(ObjectContainer.class);
		ExtObjectContainer eocMock = createMock(ExtObjectContainer.class);
		new ObjectServerHolder().setObjectServer(osMock);

		FilterChain mockFilterChain = createMock(FilterChain.class);
		ServletRequest mockRequest = createMock(ServletRequest.class);
		ServletResponse mockResponse = createMock(ServletResponse.class);

		mockFilterChain.doFilter(mockRequest, mockResponse);
		expectLastCall().andThrow(new ServletException());
		expect(osMock.openClient()).andReturn(ocMock);
		expect(ocMock.ext()).andReturn(eocMock);
		expect(eocMock.isClosed()).andReturn(false);
		expect(ocMock.close()).andReturn(true);

		replay(mockFilterChain, mockRequest, mockResponse, osMock, ocMock, eocMock);

		ServletException e = null;
		try {
			filter.doFilter(mockRequest, mockResponse, mockFilterChain);
		} catch (ServletException se) {
			e = se;
		}

		assert e != null : "ServletException was not propagated as expected";

		verify(mockFilterChain, mockRequest, mockResponse, osMock, ocMock, eocMock);
	}
}
