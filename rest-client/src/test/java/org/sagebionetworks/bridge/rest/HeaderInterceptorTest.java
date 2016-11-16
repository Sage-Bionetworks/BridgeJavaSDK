package org.sagebionetworks.bridge.rest;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import okhttp3.Interceptor.Chain;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Request.class, Response.class, Chain.class, Response.Builder.class })
public class HeaderInterceptorTest {

    private HeaderInterceptor interceptor;
    
    @Mock
    Chain chain;
    
    @Mock
    Request request;
    
    @Mock
    Builder builder;
    
    @Test
    public void testHeadersSet() throws Exception {
        doReturn(request).when(builder).build();
        doReturn(request).when(chain).request();
        doReturn(builder).when(request).newBuilder();
        interceptor = new HeaderInterceptor("app/12", "fr,en");
        
        interceptor.intercept(chain);
        
        InOrder inOrder = inOrder(builder, builder);
        inOrder.verify(builder).header("User-Agent", "app/12");
        inOrder.verify(builder).header("Accept-Language", "fr,en");
        inOrder.verify(builder).build();
        verifyNoMoreInteractions(builder);
        verify(chain).proceed(request);
    }
    
    @Test
    public void testHeadersNotSetWhenNotPresent() throws Exception {
        doReturn(request).when(builder).build();
        doReturn(request).when(chain).request();
        doReturn(builder).when(request).newBuilder();
        interceptor = new HeaderInterceptor(null, null);
        
        interceptor.intercept(chain);
        
        verify(builder).build();
        verifyNoMoreInteractions(builder);
        verify(chain).proceed(request);        
    }
}
