package com.puppetlabs.puppetdb.javaclient.impl;

import com.google.gson.*;
import com.puppetlabs.puppetdb.javaclient.model.Event;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Nik Ogura on 2014-10-24.
 */
public class EventJsonAdapter implements JsonDeserializer<Event> {
    @Override
    public Event deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject in = json.getAsJsonObject();
        GsonProvider.DateJsonAdapter dateAdapter = new GsonProvider.DateJsonAdapter();
        List<String> pathList = new ArrayList<String>();

        String[] stringFields = new String[]{
                "certname",
                "report",
                "resource-type",
                "resource-title",
                "property",
                "old-value",
                "message",
                "file",
                "containing-class",
        };

        String[] dateFields = new String[]{
                "timestamp",
                "run-start-time",
                "run-end-time",
                "report-receive-time",

        };

        Event event = new Event();

        /*
            Most of this is just boilerplate.  The new-value field is the only wildcard
         */

        // process string fields
        for (int i = 0; i < stringFields.length; i++) {
            if (!in.get(stringFields[i]).isJsonNull() ) event.setCertname(in.get(stringFields[i]).getAsString());

        }

        // process date fields
        for (int i = 0; i< dateFields.length; i++) {
            if (!in.get(dateFields[i]).isJsonNull()) event.setTimestamp(dateAdapter.deserialize(in.get(dateFields[i]), Date.class, context));

        }

        // the lonely int field
        if (!in.get("line").isJsonNull()) event.setLine(in.get("line").getAsInt());


        // containment-path
        for (JsonElement item : in.get("containment-path").getAsJsonArray()) {
            if (!item.isJsonNull()) pathList.add(item.getAsString());
        }
        event.setContainmentPath(pathList);

        // status is an enum
        event.setStatus(Event.Status.valueOf(in.get("status").getAsString()));

        if (in.get("new-value").isJsonArray()) {
            StringBuilder sb = new StringBuilder();

            JsonArray newValues = in.get("new-value").getAsJsonArray();

            if (newValues.size() == 1) {    // If it's a one element array, we'll just return the only element as a string.
                sb.append(newValues.get(0).getAsString());
            } else {
                for (int i = 0; i < newValues.size(); i++) {
                    JsonElement e = newValues.get(i);

                    if (!e.isJsonNull()) {
                        sb.append(e.getAsString());
                    }

                    if (!(i == (newValues.size() - 1))) {  //append a separator if it's not the last element
                        sb.append(" ");  // is separating things with a space safe?  will there be things with embedded spaces?  dunno.
                    }
                }
            }

            event.setNewValue(sb.toString());

        } else {
            if (!in.get("new-value").isJsonNull()) {
                event.setNewValue(in.get("new-value").getAsString());
            }
        }

        return event;
    }
}
