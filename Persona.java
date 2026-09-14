import java.util.*;


public class Persona{
	
	private String nombre;
	private String numeroTelefonico;
	
 
	public Persona(){
		Persona = new Persona();
	}

	public Persona(String nombre, String numeroTelefonico){
		this.nombre = nombre;
		this.numeroTelefonico = numeroTelefonico;
		Persona = new Persona();
	}

	//getters

	public String getNombre(){
		return nombre;
	}

	public String getNumeroTelefonico(){
		return numeroTelefonico;
	}

	//setters		

	public void setNombre(){
		this.nombre = nombre;
	}

	public void setNumeroTelefonico(){
		this.numeroTelefonico = nombre;
	}


	@overide
	public String toString(){
		return "Nombre: " + nombre "\nTelefono: " + telefono;
	}

	@overide
	public boolean equals(Object objeto){
		boolean resultado = false;
		if ((objeto != null) && (objeto instanceof Persona)){
			Persona persona = (Persona) objeto;
			if (nombre.equals(persona.getNombre()) && numeroTelefonico.equals(persona.getNumerotelefonico())){
				resultado = true;
			} 
		}
		return resultado;
	}

	
	@overide
	public int HashCode(){
		return objects.hash(nombre, numeroTelefonico)
	}
}

public static void main 
