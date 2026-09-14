import IPersonaVista;
import IPersonaDAO;

public class PersonaControl {
	
	private static IPersonaVista vista;
	private static IPersonaDAO persona = new IPersonaDaoArrayList;
	
	public static void Main() {

		int opcion;
		
		do {
			opcion = vista.seleccionarOpcion();
			
			switch(opcion) {
				case 1: // Agregar
					insertarPersona();
					break;
					
				case 2: // Modificar
					borrarPersona();
					break;
					
				case 3: // Borrar
					modificarPersona();
					break;
					
				case 4: // Consultar
					consultarPersona();
					break;
					
				case 5: // Listar
					listarPersona();
					break;
					
				case 0:
					vista.escribir("Saliendo del programa... ¡Hasta luego!");
					break;
			}
			
		} while(opcion != 0);
	}

	public static void borrarPersona(){
		String nombre = vista.leerNombre("borrar");
			
	}

	public static void modificarPersona(){
		String nombre = vista.leerNombre("modificar");
		Persona persona = vista.leerDatos(nombre);
		
		if(persona != null){
			vista.escribir("\nContacto encontrado: \n");
			vista.escribir(persona.toString() + "\n")	
		}
	}

	public static void modificarPersona(){
		String nombre = vista.leerNombre("modificar");
		Persona persona = vista.leerDatos(nombre);
		
		if(persona != null){
			vista.escribir("\nContacto encontrado: \n");
			vista.escribir(persona.toString() + "\n")	
		}
	}

	public static void listaPersona(){
		String listaPersona = personas.listar();
		if(listaPersonas.isEmpty()){
			vista.escribir("\nNo hay contactos registrados.\n");
		} else {
			vista.escribir("Lista de contactos: \n" + listaPersonas.toString());
		}
	}
}










