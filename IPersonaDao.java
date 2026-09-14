public interface IPersonaDAO{

	public boolean insertar(Persona persona);
	public boolean borrar(String nombre);
	public boolean modificar(String nombre, Persona persona);
	public Persona consultar(String nombre);
	public String listar();
	
}
