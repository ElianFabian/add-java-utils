import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

import java.io.FileWriter;
import java.io.IOException;

public class JSONWriter<T>
{
    public final String filename;
    private final Gson gson;

    public JSONWriter(String fichero)
    {
        this.filename = fichero;

        gson = new Gson();
    }

    public void write(T[] data)
    {
        try
        {
            JsonWriter jsonWriter = new JsonWriter(new FileWriter(filename));
            gson.toJson(data, data.getClass(), jsonWriter);
            jsonWriter.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
