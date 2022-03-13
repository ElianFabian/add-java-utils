import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CSVReader
{
    public final String filename;
    public Character delimiter;

    private final boolean hasHeader;
    private final HashMap<String, Integer> headerHashMap = new HashMap<>();

    public class Row
    {
        String[] content;

        public String getString(String column)
        {
            return content[headerHashMap.get(column)];
        }

        public int getInt(String column)
        {
            return Integer.parseInt(getString(column));
        }

        public float getFloat(String column)
        {
            return Float.parseFloat(getString(column));
        }
    }

    //region Constructors
    
    public CSVReader(String filename, boolean hasHeader, Character delimiter)
    {
        this.filename = filename;
        this.delimiter = delimiter;
        this.hasHeader = hasHeader;

        if (hasHeader) getHeader();
    }
    
    //endregion

    //region Methods

    public void readByPosition(Consumer<String[]> row)
    {
        String line;

        try (FileReader fr = new FileReader(filename);
             BufferedReader br = new BufferedReader(fr)
        )
        {
            if (hasHeader) br.readLine();

            while (( line = br.readLine() ) != null)
            {
                String[] currentRow = line.split(delimiter.toString());

                row.accept(currentRow);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public List<String[]> readByPosition()
    {
        String line;
        List<String[]> rows = new ArrayList<>();

        try (FileReader fr = new FileReader(filename);
             BufferedReader br = new BufferedReader(fr)
        )
        {
            if (hasHeader) br.readLine();

            while (( line = br.readLine() ) != null)
            {
                String[] currentRow = line.split(delimiter.toString());

                rows.add(currentRow);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return rows;
    }

    public void readByName(Consumer<Row> row)
    {
        String line;
        Row currentRow = new Row();

        try (FileReader fr = new FileReader(filename);
             BufferedReader br = new BufferedReader(fr)
        )
        {
            if (hasHeader) br.readLine();

            while (( line = br.readLine() ) != null)
            {
                currentRow.content = line.split(delimiter.toString());

                row.accept(currentRow);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public List<HashMap<String, String>> readByName()
    {
        String line;
        List<HashMap<String, String>> rows = new ArrayList<>();

        try (FileReader fr = new FileReader(filename);
             BufferedReader br = new BufferedReader(fr)
        )
        {
            if (hasHeader) br.readLine();

            while (( line = br.readLine() ) != null)
            {
                String[] currentRow = line.split(delimiter.toString());
                HashMap<String, String> namedRow = new HashMap<>();
                
                headerHashMap.forEach((columnName, position) ->
                {
                    namedRow.put(columnName, currentRow[position]);
                });
                
                rows.add(namedRow);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
        return rows;
    }

    public String[] findRow(Predicate<String[]> row)
    {
        String line;

        try (FileReader fr = new FileReader(filename);
             BufferedReader br = new BufferedReader(fr)
        )
        {
            if (hasHeader) br.readLine();

            while (( line = br.readLine() ) != null)
            {
                String[] currentRow = line.split(delimiter.toString());

                boolean hasBeenFound = row.test(currentRow);

                if (hasBeenFound) return currentRow;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return new String[]{ };
    }

    public List<String[]> findRows(Predicate<String[]> row)
    {
        String line;

        List<String[]> foundRows = new ArrayList<>();

        try (FileReader fr = new FileReader(filename);
             BufferedReader br = new BufferedReader(fr)
        )
        {
            if (hasHeader) br.readLine();

            while (( line = br.readLine() ) != null)
            {
                String[] currentRow = line.split(delimiter.toString());

                boolean hasBeenFound = row.test(currentRow);

                if (hasBeenFound) foundRows.add(currentRow);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return foundRows;
    }

    private void getHeader()
    {
        String line;

        try (FileReader fr = new FileReader(filename);
             BufferedReader br = new BufferedReader(fr)
        )
        {
            if (( line = br.readLine() ) != null)
            {
                String[] header = line.split(delimiter.toString());
                for (int i = 0; i < header.length; i++)
                {
                    headerHashMap.put(header[i], i);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //endregion
}
