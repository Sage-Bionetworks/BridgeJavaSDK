package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.LinkedHashSet;

import org.junit.Before;
import org.junit.Test;

public class ClientProviderTest {
    
    @Before
    public void before() {
        ClientProvider.clearLanguages();
    }
    
    @Test
    public void setLanguage() {
        ClientProvider.addLanguage("en-US");
        ClientProvider.addLanguage("fr");
        
        LinkedHashSet<String> languages = ClientProvider.getLanguages();
        
        Iterator<String> it = languages.iterator();
        String firstLang = it.next();
        String secondLang = it.next();
        
        assertEquals("en-US", firstLang);
        assertEquals("fr", secondLang);
    }

}
