package fi.hsl.common.passengercount.json;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.NumberConverter;
import com.dslplatform.json.StringConverter;

import java.util.Arrays;

public class IntegerWithNullStringConverter {
    public static final JsonReader.ReadObject<Integer> JSON_READER = reader -> {
        if (reader.last() == '"') {
            final String str = reader.readSimpleString();
            System.out.println(str);
            if ("null".equals(str)) {
                return null;
            }
        }

        return NumberConverter.deserializeInt(reader);
    };
    public static final JsonWriter.WriteObject<Integer> JSON_WRITER = (writer, value) -> {
        if (value == null) {
            //Not the correct way to use null values in JSON, but APC message format uses this..
            StringConverter.serialize("null", writer);
        } else {
            NumberConverter.serialize(value, writer);
        }
    };
}
