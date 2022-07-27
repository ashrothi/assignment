package com.rating.serializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.rating.enums.IdDisplayNameProvidable;

public class IdDisplayNameSerializer extends JsonSerializer<IdDisplayNameProvidable> {

    @Override
    public void serialize(IdDisplayNameProvidable value, JsonGenerator generator,
                          SerializerProvider provider) throws IOException,
            JsonProcessingException {
        generator.writeStartObject();
        generator.writeFieldName("id");
        generator.writeString(value.getValue());
        generator.writeFieldName("name");
        generator.writeString(value.getDisplayName());
        generator.writeEndObject();
    }

}
