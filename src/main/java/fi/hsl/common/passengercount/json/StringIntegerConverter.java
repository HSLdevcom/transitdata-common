package fi.hsl.common.passengercount.json;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.StringConverter;

class StringIntegerConverter {
    public static final JsonReader.ReadObject<Integer> JSON_READER = reader -> {
        final String value = StringConverter.deserialize(reader);
        if ("null".equals(value)) {
            return null;
        } else {
            return Integer.parseInt(value);
        }
    };
    public static final JsonWriter.WriteObject<Integer> JSON_WRITER = (writer, value) -> {
        if (value == null) {
            //Not the correct way to use null values in JSON, but APC message format uses this..
            StringConverter.serialize("null", writer);
        } else {
            StringConverter.serialize(String.valueOf(value), writer);
        }
    };
}
