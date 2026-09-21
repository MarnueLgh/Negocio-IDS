    private static final int TAMANO_NOMBRE = 30;
    private static final int TAMANO_TELEFONO = 10;
    private static final int TAMANO_REGISTRO = TAMANO_NOMBRE + TAMANO_TELEFONO; // 40 bytes
    
    // Capacidad máxima de registros del archivo hash
    private static final int MAX_REGISTROS = 100; 
    private static final Path ARCHIVO_PATH = Paths.get("contactos_hash.dat");

    // Pre-asigna el espacio en disco lleno de bytes 0 para poder escribir en cualquier posición aleatoria
    private static void inicializarArchivo() throws IOException {
        if (!ARCHIVO_PATH.toFile().exists()) {
            try (FileChannel channel = FileChannel.open(ARCHIVO_PATH, 
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                ByteBuffer buffer = ByteBuffer.allocate(TAMANO_REGISTRO * MAX_REGISTROS);
                channel.write(buffer); // Genera un archivo limpio de exactamente 4,000 bytes
            }
        }
    }

    // FUNCIÓN HASH: Convierte el nombre en un entero (índice) entre 0 y 99
    private static int calcularIndiceHash(String nombre) {
        int hash = nombre.trim().toLowerCase().hashCode();
        return Math.abs(hash) % MAX_REGISTROS;
    }

