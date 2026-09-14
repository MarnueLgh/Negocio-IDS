import java.util.*;

public class PersonaDAOHashMaps {

    private HashTable<Persona> personas = new HashTable()

    public boolean insertar(Persona personas){
        return = personas.put(persona.getNombre(), persona.getTelefono()) != null;

    }

    public boolean borrar(){
        return personas.remove(nombre) != null;
    }

    public boolean modificar(String nombre, Persona persona){
        return persona.replace(nombre, persona.getTelefono()) != null;
    } 

    public Persona consultar(String nombre){
        String telefono = personas.get(nombre);
        if (nombre == null) {
            return null;
        } else {
            return new Persona(nombre, telefono); 
        }
    }

    public String listar(){
        String listaPersonas = "";

        for (Map.Entry<String, String> entrada: personas.entrySet()){
            persona = new Persona(entrada.getKey(), entrada.getValue);
            listaPersonas += persona.toString();
        }

        return listaPersonas;

    }
}