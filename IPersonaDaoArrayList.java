import java.util.*;

public class IPersonaDaoArrayList implements IPersonaDao{

	private List<Persona> listaPersona;

	public IPersonaDaoArrayList(){
		listaPersona = new ArrayList<>();
	}
	
	public boolean insertar(Persona persona){
		return listaPersonas.add(persona);
	}

	public boolean borrar(String nombre){
		int indice = buscarNombre(nombre);
		if(indice == -1){
			return false;
		} else {
			listaPersonas.remove(indice);
		}

		return true;
	}

	public boolean modificar(String nombre, Persona persona){
		int indice = buscarNombre(nombre);
		if(indice == -1){
			return false;
		} else {
			listaPersonas.set(indice, persona.setNombre(nombre));
		}

		return true;
	}

	public Persona consultar(String nombre){
		if(indice == -1){
			return false;
		} else {
			listaPersonas.get(nmombre);
		}

		return true;
	}

	public String listar(){
		Strin lista = ""; 
		for(Persona personas: listaPersona){
			lista += persona;
		}
		return lista;
	}

	public int buscaNombre(String nombre){
		int indice;

		while(inice < listaPersona.size() && nombre != listaPersona.get(indice).getNombre(nombre)){
			indice++;
		}
		if(indice >= listaPersona.size()){return -1;} 
		return indice; 
	}	
}