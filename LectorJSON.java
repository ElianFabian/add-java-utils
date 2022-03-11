import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import java.io.FileReader;
import java.io.IOException;


public class LectorJSON<T>
{
    public final String fichero;
    private final Gson gson;
    public final Class<T[]> type;

    public LectorJSON(String fichero, Class<T[]> type)
    {
        this.fichero = fichero;
        gson = new Gson();
        
        this.type = type;
    }

    public T[] leerTodo()
    {
        try {
            JsonReader jsonReader = new JsonReader(new FileReader(fichero));
            T[] o = gson.fromJson(jsonReader, type);
            jsonReader.close();

            return o;
        }
        catch (JsonIOException | IOException | JsonSyntaxException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
