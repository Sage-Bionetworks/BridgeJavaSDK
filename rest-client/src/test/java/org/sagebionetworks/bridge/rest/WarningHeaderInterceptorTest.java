package org.sagebionetworks.bridge.rest;

import okhttp3.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Request.class, Response.class, Interceptor.Chain.class, Response.Builder.class, WarningHeaderInterceptor.class, LoggerFactory.class})
public class WarningHeaderInterceptorTest {
    private static final String TEST_WARNING_MSG_1 = "test msg 1";
    private static final String TEST_WARNING_MSG_2 = "test msg 2";
    private static final String TEST_WARNING_MSG_3 = "test msg 3";
    private static final String TEST_WARNING_MSGS = TEST_WARNING_MSG_1 + "; " + TEST_WARNING_MSG_2 + "; " + TEST_WARNING_MSG_3;
    private static final String TEST_URL = "https://www.testurl.com/api/v3/study";

    private static final String TEST_LOG_MSG_1 = TEST_URL + " " + TEST_WARNING_MSG_1;
    private static final String TEST_LOG_MSG_2 = TEST_URL + " " + TEST_WARNING_MSG_2;
    private static final String TEST_LOG_MSG_3 = TEST_URL + " " + TEST_WARNING_MSG_3;

    private static final Headers TEST_HEADERS = new Headers.Builder().add(WarningHeaderInterceptor.BRIDGE_API_STATUS_HEADER, TEST_WARNING_MSGS).build();

    @Mock
    Interceptor.Chain chain;

    @Mock
    Request request;

    @Mock
    Request.Builder builder;

    @Mock
    Response response;

    @Test
    public void testHeadersSet() throws Exception {
        mockStatic(LoggerFactory.class);
        Logger logger = mock(Logger.class);
        when(LoggerFactory.getLogger(any(Class.class))).thenReturn(logger);

        when(builder.build()).thenReturn(request);
        when(chain.request()).thenReturn(request);
        when(request.newBuilder()).thenReturn(builder);
        when(chain.proceed(request)).thenReturn(response);
        when(response.headers()).thenReturn(TEST_HEADERS);
        when(response.request()).thenReturn(request);
        when(request.url()).thenReturn(HttpUrl.parse(TEST_URL));

        new WarningHeaderInterceptor().intercept(chain);

        //verify
        verify(chain).request();
        verify(chain).proceed(request);
        verify(response).headers();
        verify(response, times(3)).request();
        verify(logger).warn(TEST_LOG_MSG_1);
        verify(logger).warn(TEST_LOG_MSG_2);
        verify(logger).warn(TEST_LOG_MSG_3);
    }
}
