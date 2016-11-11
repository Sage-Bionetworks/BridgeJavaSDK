package org.sagebionetworks.bridge.rest.gson;

import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.io.Writer;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import org.sagebionetworks.bridge.rest.model.SurveyElement;
import org.sagebionetworks.bridge.rest.model.SurveyInfoScreen;
import org.sagebionetworks.bridge.rest.model.SurveyQuestion;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;

@RunWith(PowerMockRunner.class)
public class RuntimeTypeAdapterFactoryTest {
    private static final RuntimeTypeAdapterFactory<SurveyElement> FACTORY = RuntimeTypeAdapterFactory  
            .of(SurveyElement.class, "type")
            .registerSubtype(SurveyQuestion.class, SurveyQuestion.class.getSimpleName())
            .registerSubtype(SurveyInfoScreen.class, SurveyInfoScreen.class.getSimpleName());

    @Test
    @Ignore
    public void testSuperType() throws Exception {
        TypeAdapter<SurveyElement> typeAdapter = FACTORY.create(new GsonBuilder().create(), new TypeToken<SurveyElement>() {});

        SurveyElement element = new SurveyElement();
        element.setGuid("GUID");
        
        Writer outputWriter = new StringWriter();
        JsonWriter writer = new JsonWriter(outputWriter);
        
        typeAdapter.write(writer, element);
        
        assertTrue(outputWriter.toString().contains("\"type\":\"SurveyElement\""));
    }
    
    @Test
    public void testSubType() throws Exception {
        TypeAdapter<SurveyElement> typeAdapter = FACTORY.create(new GsonBuilder().create(), new TypeToken<SurveyElement>() {});

        SurveyQuestion question = new SurveyQuestion();
        question.setTitle("This is a title");
        
        Writer outputWriter = new StringWriter();
        JsonWriter writer = new JsonWriter(outputWriter);
        
        typeAdapter.write(writer, question);
        assertTrue(outputWriter.toString().contains("\"type\":\"SurveyQuestion\""));
    }
}
