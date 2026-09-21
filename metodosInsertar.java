import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;



    private static final int LONGITUD_NOMBRE = 30;
    private static final int LONGITUD_TELEFONO = 10;
    private static final int TAMANO_REGISTRO = LONGITUD_NOMBRE + LONGITUD_TELEFONO;
    private static final int CAPACIDAD_TABLA = 100;

    private static int funcionHash(String nombre){
            int hash = nombre.trim().toLowerCase().hashCode();
            return Math.abs(hash) % CAPACIDAD_TABLA;
    }

    