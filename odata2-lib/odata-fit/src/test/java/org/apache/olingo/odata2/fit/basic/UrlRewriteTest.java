/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.apache.olingo.odata2.fit.basic;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.net.URI;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.api.processor.ODataSingleProcessor;
import org.apache.olingo.odata2.api.processor.part.MetadataProcessor;
import org.apache.olingo.odata2.api.processor.part.ServiceDocumentProcessor;
import org.apache.olingo.odata2.api.uri.info.GetMetadataUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetServiceDocumentUriInfo;
import org.apache.olingo.odata2.testutil.helper.HttpMerge;
import org.apache.olingo.odata2.testutil.helper.HttpSomethingUnsupported;
import org.apache.olingo.odata2.testutil.helper.StringHelper;
import org.apache.olingo.odata2.testutil.server.ServletType;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 */
public class UrlRewriteTest extends AbstractBasicTest {

    public UrlRewriteTest(final ServletType servletType) {
        super(servletType);
    }

    @Override
    protected ODataSingleProcessor createProcessor() throws ODataException {
        final ODataSingleProcessor processor = mock(ODataSingleProcessor.class);
        when(((MetadataProcessor) processor).readMetadata(any(GetMetadataUriInfo.class), any(String.class))).thenReturn(
                ODataResponse.entity("metadata")
                             .status(HttpStatusCodes.OK)
                             .build());
        when(((ServiceDocumentProcessor) processor).readServiceDocument(any(GetServiceDocumentUriInfo.class),
                any(String.class))).thenReturn(ODataResponse.entity("service document")
                                                            .status(HttpStatusCodes.OK)
                                                            .build());
        return processor;
    }

    @Test
    public void testGetServiceDocumentRedirect() throws Exception {
        final HttpRequestBase httpMethod = createRedirectRequest(HttpGet.class);
        final HttpResponse response = getHttpClient().execute(httpMethod);
        assertEquals(HttpStatusCodes.TEMPORARY_REDIRECT.getStatusCode(), response.getStatusLine()
                                                                                 .getStatusCode());
    }

    @Test
    public void testPutServiceDocumentRedirect() throws Exception {
        final HttpRequestBase httpMethod = createRedirectRequest(HttpPut.class);
        final HttpResponse response = getHttpClient().execute(httpMethod);
        assertEquals(HttpStatusCodes.TEMPORARY_REDIRECT.getStatusCode(), response.getStatusLine()
                                                                                 .getStatusCode());
    }

    @Test
    public void testPostServiceDocumentRedirect() throws Exception {
        final HttpRequestBase httpMethod = createRedirectRequest(HttpPost.class);
        final HttpResponse response = getHttpClient().execute(httpMethod);
        assertEquals(HttpStatusCodes.TEMPORARY_REDIRECT.getStatusCode(), response.getStatusLine()
                                                                                 .getStatusCode());
    }

    @Test
    public void testDeleteServiceDocumentRedirect() throws Exception {
        final HttpRequestBase httpMethod = createRedirectRequest(HttpDelete.class);
        final HttpResponse response = getHttpClient().execute(httpMethod);
        assertEquals(HttpStatusCodes.TEMPORARY_REDIRECT.getStatusCode(), response.getStatusLine()
                                                                                 .getStatusCode());
    }

    @Ignore("CXF OPTIONS requests are not handled for some reason and in this case the response is 404")
    @Test
    public void testOptionsServiceDocumentRedirect() throws Exception {
        final HttpRequestBase httpMethod = createRedirectRequest(HttpOptions.class);
        final HttpResponse response = getHttpClient().execute(httpMethod);
        assertEquals(HttpStatusCodes.TEMPORARY_REDIRECT.getStatusCode(), response.getStatusLine()
                                                                                 .getStatusCode());
    }

    @Test
    public void testHeadServiceDocumentRedirect() throws Exception {
        final HttpRequestBase httpMethod = createRedirectRequest(HttpHead.class);
        final HttpResponse response = getHttpClient().execute(httpMethod);
        assertEquals(HttpStatusCodes.TEMPORARY_REDIRECT.getStatusCode(), response.getStatusLine()
                                                                                 .getStatusCode());
    }

    @Test
    public void testMergeServiceDocumentRedirect() throws Exception {
        final HttpRequestBase httpMethod = createRedirectRequest(HttpMerge.class);
        final HttpResponse response = getHttpClient().execute(httpMethod);
        assertEquals(HttpStatusCodes.TEMPORARY_REDIRECT.getStatusCode(), response.getStatusLine()
                                                                                 .getStatusCode());
    }

    @Test
    public void testPatchServiceDocumentRedirect() throws Exception {
        final HttpRequestBase httpMethod = createRedirectRequest(HttpPatch.class);
        final HttpResponse response = getHttpClient().execute(httpMethod);
        assertEquals(HttpStatusCodes.TEMPORARY_REDIRECT.getStatusCode(), response.getStatusLine()
                                                                                 .getStatusCode());
    }

    @Test
    public void testSomethingUnsupportedServiceDocumentRedirect() throws Exception {
        final HttpRequestBase httpMethod = createRedirectRequest(HttpSomethingUnsupported.class);
        final HttpResponse response = getHttpClient().execute(httpMethod);
        assertEquals(HttpStatusCodes.NOT_IMPLEMENTED.getStatusCode(), response.getStatusLine()
                                                                              .getStatusCode());
    }

    private HttpRequestBase createRedirectRequest(final Class<? extends HttpRequestBase> clazz) throws Exception {
        String endpoint = getEndpoint().toASCIIString();
        endpoint = endpoint.substring(0, endpoint.length() - 1);

        final HttpRequestBase httpMethod = clazz.newInstance();
        httpMethod.setURI(URI.create(endpoint));

        final HttpParams params = new BasicHttpParams();
        params.setParameter("http.protocol.handle-redirects", false);
        httpMethod.setParams(params);
        return httpMethod;
    }

    @Test
    public void testGetServiceDocumentWithSlash() throws Exception {
        final HttpGet get = new HttpGet(URI.create(getEndpoint().toString()));
        final HttpParams params = new BasicHttpParams();
        params.setParameter("http.protocol.handle-redirects", false);
        get.setParams(params);

        final HttpResponse response = getHttpClient().execute(get);

        final String payload = StringHelper.inputStreamToString(response.getEntity()
                                                                        .getContent());
        assertEquals("service document", payload);
        assertEquals(HttpStatusCodes.OK.getStatusCode(), response.getStatusLine()
                                                                 .getStatusCode());
    }

    @Test
    public void testGetMetadata() throws Exception {
        final HttpGet get = new HttpGet(URI.create(getEndpoint().toString() + "$metadata"));
        final HttpParams params = new BasicHttpParams();
        params.setParameter("http.protocol.handle-redirects", false);
        get.setParams(params);

        final HttpResponse response = getHttpClient().execute(get);

        final String payload = StringHelper.inputStreamToString(response.getEntity()
                                                                        .getContent());
        assertEquals("metadata", payload);
        assertEquals(HttpStatusCodes.OK.getStatusCode(), response.getStatusLine()
                                                                 .getStatusCode());
    }

}
