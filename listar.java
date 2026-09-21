public String listar() {
	

	String lista = "";
	String nombre = "";
	String telefono = "";

	byte[] bytesNombre = new byte[30];
	byte[] bytesTelefono = new byte[10];

	try (FileChannel canal = FileChannel.open(Path.of("personas.dat"), StandardOpenOption.READ)) {

		ByteBuffer buffer = ByteBuffer.allocate(40);

		while (canal.read(buffer) != -1) {
		
			buffer.flip();

			buffer.get(bytesNombre);
			nombre = new String(bytesNombre, StandardCharsets.UTF_8).trim();

			buffer.get(bytesTelefono);
			telefono = new String(bytesTelefono, StandardCharsets.UTF_8).trim();

			lista += "Nombre: " + nombre + " - Teléfono: " + telefono + "\n";

			buffer.clear();
			
		}

	} catch (IOException e) {
	
		e.printStackTrace();
		
	}

	return lista;
	
}