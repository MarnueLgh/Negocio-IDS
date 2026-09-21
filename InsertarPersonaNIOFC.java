import java.io.*;
import java.nio.*;
import java.util.*;

public class InsertarPersonaNIOFC {
    
    

    public void insertar(String ruta){

        Scanner teclado = new Scanner(System.in); 
                
        System.out.println("Ingresa el nombre que quieres agregar");
        String nombreEntrada = teclado.nextLine();
        
        System.out.println("Ingresa el teléfono que quieres agregar");
        String telefonoEntrada = teclado.nextLine();


        String nombreFormateado = new String.format("%-30s", nombreEntrada);
        String telefonoFormateado = String.format("%-10s", telefonoEntrada);
        int registroMaxLenght = (30 + 10) * 2; // pregunta para la siguiente clas3e

        Path pathr = paths.get(ruta);

        try (FileChannel canal = FileChannel.open(path, Standard.Open.Option.CREATE, Standard.Open.Option.WRITE, Standard.Open.Option.APPEND)){

            ByteBuffer buffer = ByteBuffer.allocate(registroMaxLenght);

            for (int i = 0; i < nombreFormateado.length(); i++) {
                buffer.putChar(nombreFormateado.charAt(i));
            }

            for (int i = 0; i < telefonoFormateado.length(); i++) {
                buffer.putChar(telefonoFormateado.charAt(i));
            }
        
        buffer.flip();
        
        canal.write(buffer);
        
        System.out.println("¡Contacto guardado exitosamente en la agenda!");


        }
    }

}
