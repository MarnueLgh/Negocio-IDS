import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class GestionPersonas {

    private static final int LONGITUD_NOMBRE = 30;
    private static final int LONGITUD_TELEFONO = 10;
    private static final int TAMANO_REGISTRO = LONGITUD_NOMBRE + LONGITUD_TELEFONO;
    private static final int CAPACIDAD_TABLA = 101;

    public static boolean insertar(String rutaArchivo, String nombre, String telefono) {
        Path path = Paths.get(rutaArchivo);
        
        String nombreFijo = String.format("%-30s", nombre.length() > LONGITUD_NOMBRE ? nombre.substring(0, LONGITUD_NOMBRE) : nombre);
        String telefonoFijo = String.format("%-10s", telefono.length() > LONGITUD_TELEFONO ? telefono.substring(0, LONGITUD_TELEFONO) : telefono);
        
        int hash = nombre.trim().toLowerCase().hashCode();
        int indiceHash = Math.abs(hash) % CAPACIDAD_TABLA;
        long posicionByte = (long) indiceHash * TAMANO_REGISTRO;
        
        ByteBuffer buffer = ByteBuffer.allocate(TAMANO_REGISTRO);
        buffer.put(nombreFijo.getBytes(StandardCharsets.UTF_8));
        buffer.put(telefonoFijo.getBytes(StandardCharsets.UTF_8));
        buffer.flip();

        try (FileChannel fileChannel = FileChannel.open(path,
        StandardOpenOption.CREATE,
        StandardOpenOption.READ,
        StandardOpenOption.WRITE)) {
            
            fileChannel.position(posicionByte);
            
            while (buffer.hasRemaining()) {
                fileChannel.write(buffer);
            }
        
            return true;
        
        } catch (IOException e) {
            System.err.println("Error al insertar el registro en el archivo .data: " + e.getMessage());
            return false;
        }
    }
}