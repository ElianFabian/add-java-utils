import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

import java.io.FileWriter;
import java.io.IOException;

public class EscritorJSON<T>
{
    public final String fichero;
    private final Gson gson;

    public EscritorJSON(String fichero)
    {
        this.fichero = fichero;
        gson = new Gson();
    }

    public void escribir(T[] data)
    {
        try
        {
            JsonWriter jsonWriter = new JsonWriter(new FileWriter(fichero));
            gson.toJson(data, data.getClass(), jsonWriter);
            jsonWriter.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
