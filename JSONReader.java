import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import java.io.FileReader;
import java.io.IOException;


public class JSONReader<T>
{
    public final String filename;
    private final Gson gson;
    public final Class<T[]> type;

    public JSONReader(String filename, Class<T[]> type)
    {
        this.filename = filename;
        this.type = type;
        
        gson = new Gson();
    }

    public T[] read()
    {
        try {
            JsonReader jsonReader = new JsonReader(new FileReader(filename));
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
