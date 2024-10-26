package org.example.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;

public class CustomLocalDateAdapter extends TypeAdapter<LocalDate> {

    @Override
    public void write(JsonWriter jsonWriter, LocalDate localDate) throws IOException {
        if(localDate != null) {
            jsonWriter.value(localDate.toString());
        } else {
            jsonWriter.nullValue();
        }
    }

    @Override
    public LocalDate read(JsonReader jsonReader) throws IOException {
        String ld = jsonReader.nextString();
        return LocalDate.parse(ld);
    }
}
