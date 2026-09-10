public boolean insertar(Persona persona) {

    try {
	
        Path ruta = Paths.get("persona.dat");

        String datos = persona.getNombre() + "," + persona.getTelefono() + "\n";

		//byte bytes[] = datos.getBytes(); 
		//Files.write(ruta, bytes, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        Files.writeString(ruta, datos, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        return true;

    } catch (IOException e) {
	
        return false;
		
    }
	
}