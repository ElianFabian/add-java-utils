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
    public String filename;
    public Character delimiter = ',';

    private final boolean hasHeader;
    private final HashMap<String, Integer> headerHashMap = new HashMap<>();

    public class Row
    {
        String[] row;

        public String getString(String column)
        {
            return row[headerHashMap.get(column)];
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

        if(hasHeader) getHeader();
    }
    public CSVReader(String filename, boolean hasHeader)
    {
        this.filename = filename;
        this.hasHeader = hasHeader;
        
        if (hasHeader) getHeader();
    }
    //endregion

    //region Methods
    
    public void read(Consumer<String[]> row)
    {
        String line;

        try (FileReader fr = new FileReader(filename);
             BufferedReader br = new BufferedReader(fr)
        )
        {
            if (hasHeader) br.readLine();

            while (( line = br.readLine() ) != null)
            {
                String[] columns = line.split(delimiter.toString());

                row.accept(columns);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public void readUsingName(Consumer<Row> row)
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
                currentRow.row = line.split(delimiter.toString());

                row.accept(currentRow);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public String[] findRow(Predicate<String[]> fila)
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
                
                boolean hasBeenFound = fila.test(currentRow);
                
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
