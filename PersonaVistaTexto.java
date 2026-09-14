import java.util.*;

public class PersonaVistaTexto implements IPersonaVista{
	
	private Scanner lector = new Scanner(System.in);
	
	public void escribir(String texto){
		
		System.out.println(texto);
		
	}
	
	public int seleccionarOpcion(){
		
		int opcion = 0;
		
		do{
			
			try{
			
				System.out.print("""
			
				Guía telefónica
				
				1. Agregar contacto
				2. Modificar contacto
				3. Borrar contacto
				4. Consultar contacto
				5. Listar contactos
				0. Salir
				
				Selecciona una opción: """);
			
				opcion = lector.nextInt();
				
				if(opcion > 5 || opcion < 0){
					
					throw new FueraDeRangoException("Seleccion una opción entre el 0 y el 5"); 
					
				}
				
				return opcion();
				
			}catch(InputMismatchException e){
				
				escribir("Ingresar un número entre 0 y 5");
				
			}catch(FueraDeRangoException e1){
				
				escribir(e1.getMessage());
				
			}
		
		} while(opcion <= 5 && opcion != 0);
		
	}
	
	public Persona leerDatos(){
		
		String nombre = "";
		String telefono = "";
		
		do{
			
			try{
				
				
				escribir("Ingrese los siguientes datos del contacto \n");
				escribir("Nombre: ");
				nombre = lector.nextLine();
				
				escribir("Telefono: ");
				telefono = lector.nextLine();
				
				return new Persona(nombre, telefono);
				
			}catch(CampoVacioException e){
			
			}
			
		}while();
		
	}
	@overide
	public String leerNombre(String operacion){
		
		
	}
	
	
}