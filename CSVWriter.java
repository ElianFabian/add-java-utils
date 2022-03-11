import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

public class CSVWriter
{
    String filename;
    char delimiter = ',';

    //region Constructors
    public CSVWriter(String filename, char delimiter)
    {
        this.filename = filename;
        this.delimiter = delimiter;
    }

    public CSVWriter(String filename)
    {
        this.filename = filename;
    }
    //endregion

    //region Methods

    public <T> void write(List<T> list, boolean addHeader, Set<String> attributesToIgnore)
    {
        StringBuilder sbDelimiter = new StringBuilder();
        StringBuilder sbHeader = new StringBuilder();
        StringBuilder sbRow = new StringBuilder();
        StringBuilder sbRows = new StringBuilder();

        T obj = list.get(0);

        try (PrintWriter pw = new PrintWriter(filename))
        {
            //region Header
            if (addHeader)
            {
                if (attributesToIgnore == null) loopThroughFields(obj, (campo, valor) ->
                {
                    getHeader(campo, sbHeader, sbDelimiter);
                });
                else loopThroughFields(obj, (campo, valor) ->
                {
                    if (!attributesToIgnore.contains(campo.getName()))
                    {
                        getHeader(campo, sbHeader, sbDelimiter);
                    }
                });

                pw.println(sbHeader);
            }
            //endregion

            //region Rows
            for (T item : list)
            {
                sbDelimiter.setLength(0);
                sbRow.setLength(0);

                if (attributesToIgnore == null) loopThroughFields(item, (campo, valor) ->
                {
                    getRow(sbRow, sbDelimiter, valor);
                });
                else loopThroughFields(item, (field, value) ->
                {
                    if (!attributesToIgnore.contains(field.getName()))
                    {
                        getRow(sbRow, sbDelimiter, value);
                    }
                });

                sbRows.append(sbRow).append('\n');
            }
            //endregion

            pw.print(sbRows);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public <T> void write(List<T> list, boolean addHeader)
    {
        write(list, addHeader, null);
    }

    private void getHeader(Field field, StringBuilder sbHeader, StringBuilder sbDelimiter)
    {
        sbHeader.append(sbDelimiter).append(field.getName());
        sbDelimiter.setLength(1);
        sbDelimiter.setCharAt(0, delimiter);
    }

    private void getRow(StringBuilder sbRow, StringBuilder sbDelimiter, Object value)
    {
        sbRow.append(sbDelimiter).append(value);
        sbDelimiter.setLength(1);
        sbDelimiter.setCharAt(0, delimiter);
    }

    private static <T> void loopThroughFields(T object, BiConsumer<Field, Object> field_value)
    {
        // https://stackoverflow.com/questions/2989560/how-to-get-the-fields-in-an-object-via-reflection
        for (Field field : object.getClass().getDeclaredFields())
        {
            if (field_value == null) throw new NullPointerException();

            // Se ignoran los campos nulos y estáticos
            if (field == null || java.lang.reflect.Modifier.isStatic(field.getModifiers())) continue;
            field.setAccessible(true);

            Object value = null;
            try
            {
                value = field.get(object);
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
            if (value == null) continue;

            field_value.accept(field, value);
        }
    }

    //endregion
}
