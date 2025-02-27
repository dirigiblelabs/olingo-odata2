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
package org.apache.olingo.odata2.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.InputStream;
import java.util.Arrays;
import org.apache.olingo.odata2.api.ODataService;
import org.apache.olingo.odata2.api.ODataServiceFactory;
import org.apache.olingo.odata2.api.batch.BatchHandler;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.commons.ODataHttpMethod;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.exception.ODataBadRequestException;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.exception.ODataMethodNotAllowedException;
import org.apache.olingo.odata2.api.processor.ODataProcessor;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.api.processor.part.BatchProcessor;
import org.apache.olingo.odata2.api.processor.part.EntityComplexPropertyProcessor;
import org.apache.olingo.odata2.api.processor.part.EntityLinkProcessor;
import org.apache.olingo.odata2.api.processor.part.EntityLinksProcessor;
import org.apache.olingo.odata2.api.processor.part.EntityMediaProcessor;
import org.apache.olingo.odata2.api.processor.part.EntityProcessor;
import org.apache.olingo.odata2.api.processor.part.EntitySetProcessor;
import org.apache.olingo.odata2.api.processor.part.EntitySimplePropertyProcessor;
import org.apache.olingo.odata2.api.processor.part.EntitySimplePropertyValueProcessor;
import org.apache.olingo.odata2.api.processor.part.FunctionImportProcessor;
import org.apache.olingo.odata2.api.processor.part.FunctionImportValueProcessor;
import org.apache.olingo.odata2.api.processor.part.MetadataProcessor;
import org.apache.olingo.odata2.api.processor.part.ServiceDocumentProcessor;
import org.apache.olingo.odata2.core.uri.UriInfoImpl;
import org.apache.olingo.odata2.core.uri.UriType;
import org.apache.olingo.odata2.testutil.fit.BaseTest;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

/**
 * Tests for request dispatching according to URI type and HTTP method.
 *
 */
public class DispatcherTest extends BaseTest {

    public static ODataService getMockService() throws ODataException {
        ServiceDocumentProcessor serviceDocument = mock(ServiceDocumentProcessor.class);
        when(serviceDocument.readServiceDocument(any(UriInfoImpl.class), anyString())).thenAnswer(getAnswer());

        EntitySetProcessor entitySet = mock(EntitySetProcessor.class);
        when(entitySet.readEntitySet(any(UriInfoImpl.class), anyString())).thenAnswer(getAnswer());
        when(entitySet.countEntitySet(any(UriInfoImpl.class), anyString())).thenAnswer(getAnswer());
        when(entitySet.createEntity(any(UriInfoImpl.class), any(InputStream.class), anyString(), anyString())).thenAnswer(getAnswer());

        EntityProcessor entity = mock(EntityProcessor.class);
        when(entity.readEntity(any(UriInfoImpl.class), anyString())).thenAnswer(getAnswer());
        when(entity.existsEntity(any(UriInfoImpl.class), anyString())).thenAnswer(getAnswer());
        when(entity.deleteEntity(any(UriInfoImpl.class), anyString())).thenAnswer(getAnswer());
        when(entity.updateEntity(any(UriInfoImpl.class), any(InputStream.class), anyString(), anyBoolean(), anyString())).thenAnswer(
                getAnswer());

        EntityComplexPropertyProcessor entityComplexProperty = mock(EntityComplexPropertyProcessor.class);
        when(entityComplexProperty.readEntityComplexProperty(any(UriInfoImpl.class), anyString())).thenAnswer(getAnswer());
        when(entityComplexProperty.updateEntityComplexProperty(any(UriInfoImpl.class), any(InputStream.class), anyString(), anyBoolean(),
                anyString())).thenAnswer(getAnswer());

        EntitySimplePropertyProcessor entitySimpleProperty = mock(EntitySimplePropertyProcessor.class);
        when(entitySimpleProperty.readEntitySimpleProperty(any(UriInfoImpl.class), anyString())).thenAnswer(getAnswer());
        when(entitySimpleProperty.updateEntitySimpleProperty(any(UriInfoImpl.class), any(InputStream.class), anyString(),
                anyString())).thenAnswer(getAnswer());

        EntitySimplePropertyValueProcessor entitySimplePropertyValue = mock(EntitySimplePropertyValueProcessor.class);
        when(entitySimplePropertyValue.readEntitySimplePropertyValue(any(UriInfoImpl.class), anyString())).thenAnswer(getAnswer());
        when(entitySimplePropertyValue.deleteEntitySimplePropertyValue(any(UriInfoImpl.class), anyString())).thenAnswer(getAnswer());
        when(entitySimplePropertyValue.updateEntitySimplePropertyValue(any(UriInfoImpl.class), any(InputStream.class), anyString(),
                anyString())).thenAnswer(getAnswer());

        EntityLinkProcessor entityLink = mock(EntityLinkProcessor.class);
        when(entityLink.readEntityLink(any(UriInfoImpl.class), anyString())).thenAnswer(getAnswer());
        when(entityLink.existsEntityLink(any(UriInfoImpl.class), anyString())).thenAnswer(getAnswer());
        when(entityLink.deleteEntityLink(any(UriInfoImpl.class), anyString())).thenAnswer(getAnswer());
        when(entityLink.updateEntityLink(any(UriInfoImpl.class), any(InputStream.class), anyString(), anyString())).thenAnswer(getAnswer());

        EntityLinksProcessor entityLinks = mock(EntityLinksProcessor.class);
        when(entityLinks.readEntityLinks(any(UriInfoImpl.class), anyString())).thenAnswer(getAnswer());
        when(entityLinks.countEntityLinks(any(UriInfoImpl.class), anyString())).thenAnswer(getAnswer());
        when(entityLinks.createEntityLink(any(UriInfoImpl.class), any(InputStream.class), anyString(), anyString())).thenAnswer(
                getAnswer());

        MetadataProcessor metadata = mock(MetadataProcessor.class);
        when(metadata.readMetadata(any(UriInfoImpl.class), anyString())).thenAnswer(getAnswer());

        BatchProcessor batch = mock(BatchProcessor.class);
        when(batch.executeBatch(any(BatchHandler.class), anyString(), any(InputStream.class))).thenAnswer(getAnswer());

        FunctionImportProcessor functionImport = mock(FunctionImportProcessor.class);
        when(functionImport.executeFunctionImport(any(UriInfoImpl.class), anyString())).thenAnswer(getAnswer());

        FunctionImportValueProcessor functionImportValue = mock(FunctionImportValueProcessor.class);
        when(functionImportValue.executeFunctionImportValue(any(UriInfoImpl.class), anyString())).thenAnswer(getAnswer());

        EntityMediaProcessor entityMedia = mock(EntityMediaProcessor.class);
        when(entityMedia.readEntityMedia(any(UriInfoImpl.class), anyString())).thenAnswer(getAnswer());
        when(entityMedia.deleteEntityMedia(any(UriInfoImpl.class), anyString())).thenAnswer(getAnswer());
        when(entityMedia.updateEntityMedia(any(UriInfoImpl.class), any(InputStream.class), anyString(), anyString())).thenAnswer(
                getAnswer());

        ODataService service = mock(ODataService.class);
        when(service.getServiceDocumentProcessor()).thenReturn(serviceDocument);
        when(service.getEntitySetProcessor()).thenReturn(entitySet);
        when(service.getEntityProcessor()).thenReturn(entity);
        when(service.getEntityComplexPropertyProcessor()).thenReturn(entityComplexProperty);
        when(service.getEntitySimplePropertyProcessor()).thenReturn(entitySimpleProperty);
        when(service.getEntitySimplePropertyValueProcessor()).thenReturn(entitySimplePropertyValue);
        when(service.getEntityLinkProcessor()).thenReturn(entityLink);
        when(service.getEntityLinksProcessor()).thenReturn(entityLinks);
        when(service.getMetadataProcessor()).thenReturn(metadata);
        when(service.getBatchProcessor()).thenReturn(batch);
        when(service.getFunctionImportProcessor()).thenReturn(functionImport);
        when(service.getFunctionImportValueProcessor()).thenReturn(functionImportValue);
        when(service.getEntityMediaProcessor()).thenReturn(entityMedia);

        return service;
    }

    private static Answer<ODataResponse> getAnswer() {
        return invocation -> mockResponse(invocation.getMethod()
                                                    .getName());
    }

    private static ODataResponse mockResponse(final String value) {
        ODataResponse response = mock(ODataResponse.class);
        when(response.getStatus()).thenReturn(HttpStatusCodes.PAYMENT_REQUIRED);
        when(response.getEntity()).thenReturn(value);

        return response;
    }

    private static UriInfoImpl mockUriInfo(final UriType uriType, final boolean isValue) throws EdmException {
        UriInfoImpl uriInfo = mock(UriInfoImpl.class);
        when(uriInfo.getUriType()).thenReturn(uriType);
        when(uriInfo.isValue()).thenReturn(isValue);
        return uriInfo;
    }

    private static void checkDispatch(final ODataHttpMethod method, final UriType uriType, final boolean isValue,
            final String expectedMethodName) throws ODataException {
        ODataServiceFactory factory = mock(ODataServiceFactory.class);

        InputStream body = mock(InputStream.class);
        final ODataResponse response =
                new Dispatcher(factory, getMockService()).dispatch(method, mockUriInfo(uriType, isValue), body, "application/xml", "*/*");
        assertEquals(expectedMethodName, response.getEntity());
    }

    private static void checkDispatch(final ODataHttpMethod method, final UriType uriType, final String expectedMethodName)
            throws ODataException {
        checkDispatch(method, uriType, false, expectedMethodName);
    }

    private static void wrongDispatch(final ODataHttpMethod method, final UriType uriType) {
        try {
            checkDispatch(method, uriType, null);
            fail("Expected ODataException not thrown");
        } catch (ODataMethodNotAllowedException e) {
            assertNotNull(e);
        } catch (ODataException e) {
            fail("Expected ODataMethodNotAllowedException not thrown");
        }
    }

    private static void notSupportedDispatch(final ODataHttpMethod method, final UriType uriType) {
        try {
            checkDispatch(method, uriType, null);
            fail("Expected ODataException not thrown");
        } catch (ODataBadRequestException e) {
            assertNotNull(e);
        } catch (ODataException e) {
            fail("Expected ODataBadRequestException not thrown");
        }
    }

    @Test
    public void dispatch() throws Exception {
        checkDispatch(ODataHttpMethod.GET, UriType.URI0, "readServiceDocument");

        checkDispatch(ODataHttpMethod.GET, UriType.URI1, "readEntitySet");
        checkDispatch(ODataHttpMethod.POST, UriType.URI1, "createEntity");

        checkDispatch(ODataHttpMethod.GET, UriType.URI2, "readEntity");
        checkDispatch(ODataHttpMethod.PUT, UriType.URI2, "updateEntity");
        checkDispatch(ODataHttpMethod.DELETE, UriType.URI2, "deleteEntity");
        checkDispatch(ODataHttpMethod.PATCH, UriType.URI2, "updateEntity");
        checkDispatch(ODataHttpMethod.MERGE, UriType.URI2, "updateEntity");

        checkDispatch(ODataHttpMethod.GET, UriType.URI3, "readEntityComplexProperty");
        checkDispatch(ODataHttpMethod.PUT, UriType.URI3, "updateEntityComplexProperty");
        checkDispatch(ODataHttpMethod.PATCH, UriType.URI3, "updateEntityComplexProperty");
        checkDispatch(ODataHttpMethod.MERGE, UriType.URI3, "updateEntityComplexProperty");

        checkDispatch(ODataHttpMethod.GET, UriType.URI4, "readEntitySimpleProperty");
        checkDispatch(ODataHttpMethod.PUT, UriType.URI4, "updateEntitySimpleProperty");
        checkDispatch(ODataHttpMethod.PATCH, UriType.URI4, "updateEntitySimpleProperty");
        checkDispatch(ODataHttpMethod.MERGE, UriType.URI4, "updateEntitySimpleProperty");
        checkDispatch(ODataHttpMethod.GET, UriType.URI4, true, "readEntitySimplePropertyValue");
        checkDispatch(ODataHttpMethod.PUT, UriType.URI4, true, "updateEntitySimplePropertyValue");
        checkDispatch(ODataHttpMethod.DELETE, UriType.URI4, true, "deleteEntitySimplePropertyValue");
        checkDispatch(ODataHttpMethod.PATCH, UriType.URI4, true, "updateEntitySimplePropertyValue");
        checkDispatch(ODataHttpMethod.MERGE, UriType.URI4, true, "updateEntitySimplePropertyValue");

        checkDispatch(ODataHttpMethod.GET, UriType.URI5, "readEntitySimpleProperty");
        checkDispatch(ODataHttpMethod.PUT, UriType.URI5, "updateEntitySimpleProperty");
        checkDispatch(ODataHttpMethod.PATCH, UriType.URI5, "updateEntitySimpleProperty");
        checkDispatch(ODataHttpMethod.MERGE, UriType.URI5, "updateEntitySimpleProperty");
        checkDispatch(ODataHttpMethod.GET, UriType.URI5, true, "readEntitySimplePropertyValue");
        checkDispatch(ODataHttpMethod.PUT, UriType.URI5, true, "updateEntitySimplePropertyValue");
        checkDispatch(ODataHttpMethod.DELETE, UriType.URI5, true, "deleteEntitySimplePropertyValue");
        checkDispatch(ODataHttpMethod.PATCH, UriType.URI5, true, "updateEntitySimplePropertyValue");
        checkDispatch(ODataHttpMethod.MERGE, UriType.URI5, true, "updateEntitySimplePropertyValue");

        checkDispatch(ODataHttpMethod.GET, UriType.URI6A, "readEntity");

        checkDispatch(ODataHttpMethod.GET, UriType.URI6B, "readEntitySet");
        checkDispatch(ODataHttpMethod.POST, UriType.URI6B, "createEntity");

        checkDispatch(ODataHttpMethod.GET, UriType.URI7A, "readEntityLink");
        checkDispatch(ODataHttpMethod.PUT, UriType.URI7A, "updateEntityLink");
        checkDispatch(ODataHttpMethod.DELETE, UriType.URI7A, "deleteEntityLink");
        checkDispatch(ODataHttpMethod.PATCH, UriType.URI7A, "updateEntityLink");
        checkDispatch(ODataHttpMethod.MERGE, UriType.URI7A, "updateEntityLink");

        checkDispatch(ODataHttpMethod.GET, UriType.URI7B, "readEntityLinks");
        checkDispatch(ODataHttpMethod.POST, UriType.URI7B, "createEntityLink");

        checkDispatch(ODataHttpMethod.GET, UriType.URI8, "readMetadata");

        checkDispatch(ODataHttpMethod.POST, UriType.URI9, "executeBatch");

        checkDispatch(ODataHttpMethod.GET, UriType.URI10, "executeFunctionImport");
        checkDispatch(ODataHttpMethod.GET, UriType.URI10a, "executeFunctionImport");
        checkDispatch(ODataHttpMethod.GET, UriType.URI11, "executeFunctionImport");
        checkDispatch(ODataHttpMethod.GET, UriType.URI12, "executeFunctionImport");
        checkDispatch(ODataHttpMethod.GET, UriType.URI13, "executeFunctionImport");
        checkDispatch(ODataHttpMethod.GET, UriType.URI14, "executeFunctionImport");
        checkDispatch(ODataHttpMethod.GET, UriType.URI14, true, "executeFunctionImportValue");

        checkDispatch(ODataHttpMethod.GET, UriType.URI15, "countEntitySet");

        checkDispatch(ODataHttpMethod.GET, UriType.URI16, "existsEntity");

        checkDispatch(ODataHttpMethod.GET, UriType.URI17, "readEntityMedia");
        checkDispatch(ODataHttpMethod.PUT, UriType.URI17, "updateEntityMedia");
        checkDispatch(ODataHttpMethod.DELETE, UriType.URI17, "deleteEntityMedia");

        checkDispatch(ODataHttpMethod.GET, UriType.URI50A, "existsEntityLink");

        checkDispatch(ODataHttpMethod.GET, UriType.URI50B, "countEntityLinks");
    }

    @Test
    public void dispatchNotAllowedCombinations() throws Exception {
        wrongDispatch(null, UriType.URI0);

        wrongDispatch(ODataHttpMethod.PUT, UriType.URI0);
        wrongDispatch(ODataHttpMethod.POST, UriType.URI0);
        wrongDispatch(ODataHttpMethod.DELETE, UriType.URI0);
        wrongDispatch(ODataHttpMethod.PATCH, UriType.URI0);
        wrongDispatch(ODataHttpMethod.MERGE, UriType.URI0);

        wrongDispatch(ODataHttpMethod.PUT, UriType.URI1);
        wrongDispatch(ODataHttpMethod.DELETE, UriType.URI1);
        wrongDispatch(ODataHttpMethod.PATCH, UriType.URI1);
        wrongDispatch(ODataHttpMethod.MERGE, UriType.URI1);

        wrongDispatch(ODataHttpMethod.POST, UriType.URI2);

        wrongDispatch(ODataHttpMethod.POST, UriType.URI3);
        wrongDispatch(ODataHttpMethod.DELETE, UriType.URI3);

        wrongDispatch(ODataHttpMethod.POST, UriType.URI4);
        wrongDispatch(ODataHttpMethod.DELETE, UriType.URI4);

        wrongDispatch(ODataHttpMethod.POST, UriType.URI5);
        wrongDispatch(ODataHttpMethod.DELETE, UriType.URI5);

        wrongDispatch(ODataHttpMethod.POST, UriType.URI6A);

        wrongDispatch(ODataHttpMethod.PUT, UriType.URI6B);
        wrongDispatch(ODataHttpMethod.DELETE, UriType.URI6B);
        wrongDispatch(ODataHttpMethod.PATCH, UriType.URI6B);
        wrongDispatch(ODataHttpMethod.MERGE, UriType.URI6B);

        wrongDispatch(ODataHttpMethod.POST, UriType.URI7A);

        wrongDispatch(ODataHttpMethod.PUT, UriType.URI7B);
        wrongDispatch(ODataHttpMethod.DELETE, UriType.URI7B);
        wrongDispatch(ODataHttpMethod.PATCH, UriType.URI7B);
        wrongDispatch(ODataHttpMethod.MERGE, UriType.URI7B);

        wrongDispatch(ODataHttpMethod.PUT, UriType.URI8);
        wrongDispatch(ODataHttpMethod.POST, UriType.URI8);
        wrongDispatch(ODataHttpMethod.DELETE, UriType.URI8);
        wrongDispatch(ODataHttpMethod.PATCH, UriType.URI8);
        wrongDispatch(ODataHttpMethod.MERGE, UriType.URI8);

        wrongDispatch(ODataHttpMethod.GET, UriType.URI9);
        wrongDispatch(ODataHttpMethod.PUT, UriType.URI9);
        wrongDispatch(ODataHttpMethod.DELETE, UriType.URI9);
        wrongDispatch(ODataHttpMethod.PATCH, UriType.URI9);
        wrongDispatch(ODataHttpMethod.MERGE, UriType.URI9);

        wrongDispatch(ODataHttpMethod.PUT, UriType.URI15);
        wrongDispatch(ODataHttpMethod.POST, UriType.URI15);
        wrongDispatch(ODataHttpMethod.DELETE, UriType.URI15);
        wrongDispatch(ODataHttpMethod.PATCH, UriType.URI15);
        wrongDispatch(ODataHttpMethod.MERGE, UriType.URI15);

        wrongDispatch(ODataHttpMethod.PUT, UriType.URI16);
        wrongDispatch(ODataHttpMethod.POST, UriType.URI16);
        wrongDispatch(ODataHttpMethod.DELETE, UriType.URI16);
        wrongDispatch(ODataHttpMethod.PATCH, UriType.URI16);
        wrongDispatch(ODataHttpMethod.MERGE, UriType.URI16);

        wrongDispatch(ODataHttpMethod.POST, UriType.URI17);
        wrongDispatch(ODataHttpMethod.PATCH, UriType.URI17);
        wrongDispatch(ODataHttpMethod.MERGE, UriType.URI17);

        wrongDispatch(ODataHttpMethod.PUT, UriType.URI50A);
        wrongDispatch(ODataHttpMethod.POST, UriType.URI50A);
        wrongDispatch(ODataHttpMethod.DELETE, UriType.URI50A);
        wrongDispatch(ODataHttpMethod.PATCH, UriType.URI50A);
        wrongDispatch(ODataHttpMethod.MERGE, UriType.URI50A);

        wrongDispatch(ODataHttpMethod.PUT, UriType.URI50B);
        wrongDispatch(ODataHttpMethod.POST, UriType.URI50B);
        wrongDispatch(ODataHttpMethod.DELETE, UriType.URI50B);
        wrongDispatch(ODataHttpMethod.PATCH, UriType.URI50B);
        wrongDispatch(ODataHttpMethod.MERGE, UriType.URI50B);
    }

    @Test
    public void dispatchNotSupportedCombinations() throws Exception {
        notSupportedDispatch(ODataHttpMethod.PUT, UriType.URI6A);
        notSupportedDispatch(ODataHttpMethod.DELETE, UriType.URI6A);
        notSupportedDispatch(ODataHttpMethod.PATCH, UriType.URI6A);
        notSupportedDispatch(ODataHttpMethod.MERGE, UriType.URI6A);
    }

    private static void checkFeature(final UriType uriType, final boolean isValue, final Class<? extends ODataProcessor> feature)
            throws ODataException {
        ODataServiceFactory factory = mock(ODataServiceFactory.class);
        new Dispatcher(factory, getMockService());
        assertEquals(feature, Dispatcher.mapUriTypeToProcessorFeature(mockUriInfo(uriType, isValue)));
        assertEquals(feature, Dispatcher.mapUriTypeToProcessorFeature(mockUriInfo(uriType, isValue)));
    }

    @Test
    public void processorFeature() throws Exception {
        checkFeature(UriType.URI0, false, ServiceDocumentProcessor.class);
        checkFeature(UriType.URI1, false, EntitySetProcessor.class);
        checkFeature(UriType.URI2, false, EntityProcessor.class);
        checkFeature(UriType.URI3, false, EntityComplexPropertyProcessor.class);
        checkFeature(UriType.URI4, false, EntitySimplePropertyProcessor.class);
        checkFeature(UriType.URI4, true, EntitySimplePropertyValueProcessor.class);
        checkFeature(UriType.URI5, false, EntitySimplePropertyProcessor.class);
        checkFeature(UriType.URI5, true, EntitySimplePropertyValueProcessor.class);
        checkFeature(UriType.URI6A, false, EntityProcessor.class);
        checkFeature(UriType.URI6B, false, EntitySetProcessor.class);
        checkFeature(UriType.URI7A, false, EntityLinkProcessor.class);
        checkFeature(UriType.URI7B, false, EntityLinksProcessor.class);
        checkFeature(UriType.URI8, false, MetadataProcessor.class);
        checkFeature(UriType.URI9, false, BatchProcessor.class);
        checkFeature(UriType.URI10, false, FunctionImportProcessor.class);
        checkFeature(UriType.URI11, false, FunctionImportProcessor.class);
        checkFeature(UriType.URI12, false, FunctionImportProcessor.class);
        checkFeature(UriType.URI13, false, FunctionImportProcessor.class);
        checkFeature(UriType.URI14, false, FunctionImportProcessor.class);
        checkFeature(UriType.URI14, true, FunctionImportValueProcessor.class);
        checkFeature(UriType.URI15, false, EntitySetProcessor.class);
        checkFeature(UriType.URI16, false, EntityProcessor.class);
        checkFeature(UriType.URI17, false, EntityMediaProcessor.class);
        checkFeature(UriType.URI50A, false, EntityLinkProcessor.class);
        checkFeature(UriType.URI50B, false, EntityLinksProcessor.class);
    }

    @Test
    public void contentNegotiationDefaultCharset() throws Exception {
        negotiateContentTypeCharset("application/xml", "application/xml;charset=utf-8", false);
    }

    @Test
    public void contentNegotiationDefaultCharsetAsDollarFormat() throws Exception {
        negotiateContentTypeCharset("application/xml", "application/xml;charset=utf-8", true);
    }

    @Test
    public void contentNegotiationSupportedCharset() throws Exception {
        negotiateContentTypeCharset("application/xml;charset=utf-8", "application/xml;charset=utf-8", false);
    }

    @Test
    public void contentNegotiationSupportedCharsetAsDollarFormat() throws Exception {
        negotiateContentTypeCharset("application/xml;charset=utf-8", "application/xml;charset=utf-8", true);
    }

    @SuppressWarnings("unchecked")
    private void negotiateContentTypeCharset(final String requestType, final String supportedType, final boolean asFormat)
            throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException, ODataException {

        ODataServiceFactory factory = mock(ODataServiceFactory.class);
        ODataService service = Mockito.mock(ODataService.class);
        Dispatcher dispatcher = new Dispatcher(factory, service);

        UriInfoImpl uriInfo = new UriInfoImpl();
        uriInfo.setUriType(UriType.URI1); //
        if (asFormat) {
            uriInfo.setFormat(requestType);
        }

        Mockito.when(service.getSupportedContentTypes(any(Class.class)))
               .thenReturn(Arrays.asList(supportedType));
        EntitySetProcessor processor = Mockito.mock(EntitySetProcessor.class);
        ODataResponse response = Mockito.mock(ODataResponse.class);
        Mockito.when(response.getContentHeader())
               .thenReturn(supportedType);
        Mockito.when(processor.readEntitySet(uriInfo, supportedType))
               .thenReturn(response);
        Mockito.when(service.getEntitySetProcessor())
               .thenReturn(processor);

        InputStream content = null;
        ODataResponse odataResponse = dispatcher.dispatch(ODataHttpMethod.GET, uriInfo, content, requestType, supportedType);
        assertEquals(supportedType, odataResponse.getContentHeader());
    }
}
