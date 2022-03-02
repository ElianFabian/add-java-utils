package archivos;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class LectorCSV
{
    String ficheroCSV;
    String separador = ",";

    //region Constrcutores
    public LectorCSV(String ficheroCSV, String separador)
    {
        this.ficheroCSV = ficheroCSV;
        this.separador = separador;
    }
    public LectorCSV(String ficheroCSV)
    {
        this.ficheroCSV = ficheroCSV;
    }
    //endregion

    //region Métodos
    
    /**
     * Lee cada fila del array.
     *
     * @param fila Es un array con los valores de las columnas de la fila que se está leyendo.
     */
    public void leerFilas(Consumer<String[]> fila)
    {
        String linea = "";

        try (FileReader fr = new FileReader(ficheroCSV);
             BufferedReader br = new BufferedReader(fr)
        )
        {
            while (( linea = br.readLine() ) != null)
            {
                String[] columnasCSV = linea.split(separador);

                fila.accept(columnasCSV);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Devuelve la primera ocurrencia que cumple la condición indicada.
     */
    public String[] encontrarFila(Function<String[], Boolean> fila)
    {
        String linea = "";

        try (FileReader fr = new FileReader(ficheroCSV);
             BufferedReader br = new BufferedReader(fr)
        )
        {
            while (( linea = br.readLine() ) != null)
            {
                String[] filaActual = linea.split(separador);
                boolean seHaEncontrado = fila.apply(filaActual);

                if (seHaEncontrado) return filaActual;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return new String[]{ };
    }

    /**
     * Devuelve todas las ocurrencias que cumplen la condición indicada.
     */
    public List<String[]> encontrarTodasLasFilas(Function<String[], Boolean> fila)
    {
        String linea = "";

        List<String[]> filasEncontradas = new ArrayList<>();

        try (FileReader fr = new FileReader(ficheroCSV);
             BufferedReader br = new BufferedReader(fr)
        )
        {
            while (( linea = br.readLine() ) != null)
            {
                String[] filaActual = linea.split(separador);

                boolean seHaEncontrado = fila.apply(filaActual);

                if (seHaEncontrado) filasEncontradas.add(filaActual);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return filasEncontradas;
    }
    
    //endregion
}
