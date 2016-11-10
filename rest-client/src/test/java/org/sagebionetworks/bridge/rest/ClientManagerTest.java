package org.sagebionetworks.bridge.rest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import org.sagebionetworks.bridge.rest.model.Environment;
import org.sagebionetworks.bridge.rest.model.SignIn;

import com.google.common.collect.Lists;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Config.class})
public class ClientManagerTest {

    @Mock
    Config config;
    
    @Mock
    ClientManager.ClientSupplier supplier;
    
    @Test
    public void testInitFromConfig() {
        SignIn accountSignIn = new SignIn().study("study-identifier").email("account@email.com")
                .password("account-password");
        SignIn adminSignIn = new SignIn().study("study-identifier").email("admin@email.com").password("admin-password");
        
        doReturn(accountSignIn).when(config).getAccountSignIn();
        doReturn(adminSignIn).when(config).getAdminSignIn();
        doReturn("study-identifier").when(config).getStudyIdentifier();
        doReturn("1").when(config).getSdkVersion();
        doReturn("account@email.com").when(config).getAccountEmail();
        doReturn("account-password").when(config).getAccountPassword();
        doReturn("admin@email.com").when(config).getAdminEmail();
        doReturn("admin-password").when(config).getAdminPassword();
        doReturn("fr,en").when(config).getLanguages();
        doReturn("dudeski").when(config).getDevName();
        doReturn(Environment.PRODUCTION).when(config).getEnvironment();
        doReturn("debug").when(config).getLogLevel();
        
        ClientManager manager = new ClientManager.Builder().withConfig(config)
                .withClientSupplier(supplier).build();
        
        assertEquals("study-identifier", manager.getConfig().getStudyIdentifier());
        assertEquals("1", manager.getConfig().getSdkVersion());
        assertEquals(accountSignIn, manager.getConfig().getAccountSignIn());
        assertEquals(adminSignIn, manager.getConfig().getAdminSignIn());
        assertEquals(Lists.newArrayList("fr","en"), manager.getAcceptedLanguages());
        assertEquals(Environment.PRODUCTION, manager.getConfig().getEnvironment());
        assertEquals("dudeski", manager.getConfig().getDevName());
        assertEquals("debug", manager.getConfig().getLogLevel());
    }
}
