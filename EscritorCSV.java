import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

public class EscritorCSV
{
    String ficheroCSV;
    char separador = ',';

    //region Constructores
    public EscritorCSV(String ficheroCSV, char separador)
    {
        this.ficheroCSV = ficheroCSV;
        this.separador = separador;
    }

    public EscritorCSV(String ficheroCSV)
    {
        this.ficheroCSV = ficheroCSV;
    }
    //endregion

    //region Métodos

    /**
     * Dado una lista de objetos los escribe en un fichero CSV.
     */
    public <T> void escribirLineas(List<T> lista, boolean aniadirEncabezado, Set<String> atributosAIgnorar)
    {
        StringBuilder sbSeparador = new StringBuilder();
        StringBuilder sbEncabezado = new StringBuilder();
        StringBuilder sbRegistro = new StringBuilder();
        StringBuilder sbRegistros = new StringBuilder();

        T obj = lista.get(0);

        try (PrintWriter pw = new PrintWriter(ficheroCSV))
        {
            if (aniadirEncabezado)
            {
                if (atributosAIgnorar == null) recorrerCampos(obj, (campo, valor) ->
                {
                    obtenerEncabezado(campo, sbEncabezado, sbSeparador);
                });
                else recorrerCampos(obj, (campo, valor) ->
                {
                    if (!atributosAIgnorar.contains(campo.getName()))
                    {
                        obtenerEncabezado(campo, sbEncabezado, sbSeparador);
                    }
                });

                pw.println(sbEncabezado);
            }

            for (T item : lista)
            {
                sbSeparador.setLength(0);
                sbRegistro.setLength(0);

                if (atributosAIgnorar == null) recorrerCampos(item, (campo, valor) ->
                {
                    obtenerRegistro(sbRegistro, sbSeparador, valor);
                });
                else recorrerCampos(item, (campo, valor) ->
                {
                    if (!atributosAIgnorar.contains(campo.getName()))
                    {
                        obtenerRegistro(sbRegistro, sbSeparador, valor);
                    }
                });

                sbRegistros.append(sbRegistro).append('\n');
            }

            pw.print(sbRegistros);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public <T> void escribirLineas(List<T> lista, boolean aniadirEncabezado)
    {
        escribirLineas(lista, aniadirEncabezado, null);
    }

    private void obtenerEncabezado(Field campo, StringBuilder sbEncabezado, StringBuilder sbSeparador)
    {
        sbEncabezado.append(sbSeparador).append(campo.getName());
        sbSeparador.setLength(1);
        sbSeparador.setCharAt(0, separador);
    }

    private void obtenerRegistro(StringBuilder sbRegistro, StringBuilder sbSeparador, Object valor)
    {
        sbRegistro.append(sbSeparador).append(valor);
        sbSeparador.setLength(1);
        sbSeparador.setCharAt(0, separador);
    }

    private static <T> void recorrerCampos(T objeto, BiConsumer<Field, Object> campo_valor)
    {
        // https://stackoverflow.com/questions/2989560/how-to-get-the-fields-in-an-object-via-reflection
        for (Field campo : objeto.getClass().getDeclaredFields())
        {
            if (campo_valor == null) throw new NullPointerException();

            // Se ignoran los campos nulos y estáticos
            if (campo == null || java.lang.reflect.Modifier.isStatic(campo.getModifiers())) continue;
            campo.setAccessible(true);

            Object valor = null;
            try
            {
                valor = campo.get(objeto);
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
            if (valor == null) continue;

            campo_valor.accept(campo, valor);
        }
    }

    //endregion
}
