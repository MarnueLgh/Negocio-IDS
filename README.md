# Convenciones de Desarrollo

**Universidad Nacional Autónoma de México**
**Facultad de Contaduría y Administración**

**Sistema de Compra, Venta e Inventario**

| | |
|---|---|
| **Tecnologías** | Java + MySQL |
| **Metodología** | Extreme Programming (XP) + Scrum |
| **Versión** | 1.0 |
| **Fecha** | 21 de agosto de 2026 |

---

## 1. Objetivo

Este documento establece las convenciones técnicas, de programación, documentación, base de datos, integración y organización que deberán seguir los tres equipos responsables del desarrollo de los subsistemas de Compras, Ventas e Inventario.

El objetivo es mantener una estructura uniforme que permita que cualquier integrante pueda comprender, modificar, integrar, probar y mantener el código de cualquiera de los tres subsistemas, incluso cuando exista una rotación de integrantes entre equipos.

Estas reglas deberán aplicarse durante todo el desarrollo. Cualquier cambio que afecte a más de un subsistema deberá ser acordado por los equipos involucrados y documentado antes de modificar el código o la base de datos.

---

## 2. Alcance y organización de los equipos

Las convenciones abarcan:

- Código Java.
- Clases, interfaces, enumeraciones, atributos, constantes y métodos.
- Parámetros y variables locales.
- Paquetes.
- Documentación de clases y métodos mediante Javadoc.
- Manejo de excepciones.
- Acceso a MySQL.
- Nombres de tablas, columnas, llaves y constraints.
- Relaciones entre subsistemas.
- Estructura general del proyecto.
- Reglas de integración y control de versiones.
- Criterios mínimos de calidad y revisión.
- Organización y almacenamiento de evidencias de las reuniones Scrum.

El proyecto estará dividido en tres equipos:

**Equipo 1:**

- Becerra Cruz Lizeth Adriana
- González de Santiago Zurisadai
- González Saucedo Oscar
- Islas López Isis Jezabel

**Equipo 2:**

- Álvarez Aguilar Saúl Antonio
- González Cruz Daniel
- Olvera Ramírez Anuar Manuel
- Pérez Flores Roberto Carlos
- Pérez Muñoz Diego

**Equipo 3:**

- Archundia Manzano Ian Aaron
- Torres Peña Jonathan Saul Alí
- Zilli Montero Carlos

Cada equipo será responsable del subsistema que le haya sido asignado. Las modificaciones que afecten componentes compartidos deberán ser comunicadas y coordinadas con los demás equipos.

---

## 3. Idioma y estilo general

**3.1.** Todo identificador, documentación y comentario creado por el equipo deberá escribirse únicamente en español, solo se utilizarán palabras de otro idioma cuando correspondan a palabras clave, palabras reservadas, conceptos técnicos, notación, nombres propios de tecnologías o elementos que necesariamente requieran otro idioma.

**3.2.** Los identificadores no deberán contener abreviaturas innecesarias, cada nombre deberá permitir comprender claramente qué representa sin necesidad de consultar documentación adicional.

**3.3.** Los identificadores deberán ser descriptivos y estar relacionados con el dominio del sistema.

**3.4.** Los identificadores de Java y SQL no deberán utilizar espacios, acentos, caracteres especiales ni la letra ñ. Los comentarios y la documentación sí deberán utilizar correctamente los caracteres del idioma español.

**3.5.** No se mezclarán idiomas dentro de un mismo identificador. Cuando un concepto pueda expresarse completamente en español, se utilizará su forma en español.

**3.6.** Los nombres deberán mantener el mismo significado en todo el proyecto. Un concepto no deberá recibir nombres diferentes en clases, métodos, tablas o documentación sin una razón técnica documentada.

**3.7.** La zona horaria establecida para la totalidad del proyecto es la correspondiente a la hora de la Ciudad de México, es decir GMT-6.

---

## 4. Convenciones para Java

### 4.1. Clases

Las clases deberán:

- Utilizar nombres en español.
- Utilizar Upper Camel Case (PascalCase).
- Representar sustantivos, entidades o conceptos del dominio.
- Evitar abreviaturas.
- Mantener un nombre que permita identificar claramente su responsabilidad.

### 4.2. Interfaces

Las interfaces deberán:

- Utilizar nombres en español.
- Utilizar Upper Camel Case.
- Todas deben de empezar con la letra I, que representa la i de interfaz.
- Representar contratos o capacidades del sistema.

### 4.3. Enumeraciones

Las enumeraciones deberán utilizar Upper Camel Case.

Los valores de las enumeraciones deberán escribirse en MAYUSCULAS_CON_GUION_BAJO.

### 4.4. Atributos

Los atributos deberán:

- Estar escritos en español.
- Utilizar Lower Camel Case.
- Tener nombres descriptivos.
- Evitar abreviaturas.
- Representar claramente el dato almacenado.

### 4.5. Variables locales

Las variables locales deberán utilizar Lower Camel Case y nombres que identifiquen claramente el dato que almacenan.

### 4.6. Parámetros

Los parámetros deberán utilizar Lower Camel Case y deberán indicar claramente qué representan.

### 4.7. Métodos

Los métodos deberán:

- Estar escritos en español.
- Utilizar Lower Camel Case.
- Iniciar con un verbo (principalmente en infinitivo) que corresponda a la responsabilidad que tienen los métodos.
- Ser suficientemente descriptivos.
- Representar una sola responsabilidad.

No se utilizarán nombres ambiguos que no permitan determinar qué realiza el método.

### 4.8. Getters y setters

Los métodos de acceso deberán utilizar nombres en español.

Para obtener un atributo se utilizará el prefijo `get` y para establecer su valor se utilizará el prefijo `set`.

### 4.9. Constantes

Las constantes deberán escribirse en MAYUSCULAS_CON_GUION_BAJO.

### 4.10. Atributos estáticos

Los atributos `static` que no sean constantes deberán utilizar Lower Camel Case.

### 4.11. Tipos de datos

Los valores monetarios deberán utilizar `Double` en Java.

Para fechas y horas se utilizarán las clases apropiadas de `java.time`, principalmente `LocalDate` y `LocalDateTime`, de acuerdo con la naturaleza del dato.

---

## 5. Convenciones para paquetes

Los paquetes deberán escribirse completamente en minúsculas.

La estructura de paquetes deberá mantener una organización uniforme entre los tres subsistemas.

Cada subsistema podrá organizar sus clases por responsabilidades, tales como modelo, acceso a datos, servicio, controlador y utilidad.

La creación de nuevos paquetes deberá respetar la estructura acordada para el proyecto y no deberá generar organizaciones diferentes entre subsistemas que realicen la misma función.

---

## 6. Responsabilidad de las clases

Cada clase deberá tener una responsabilidad clara y delimitada.

Las clases de utilidad contendrán funciones auxiliares reutilizables que no pertenezcan a una entidad o servicio específico.

No se concentrará toda la lógica de un subsistema en una sola clase.

---

## 7. Convenciones de base de datos MySQL

### 7.1. Tablas

Las tablas deberán nombrarse en español, utilizando minúsculas y Upper Camel Case.

No se utilizarán nombres ambiguos o genéricos para las tablas.

### 7.2. Columnas

Las columnas deberán escribirse en minúsculas utilizando Lower Camel Case, dicho nombre no debe ser ambiguo o genérico.

### 7.3. Llaves primarias

En Java, los identificadores de las llaves primarias seguirán la convención `nombreTablaId`.

En MySQL, las columnas de las llaves primarias utilizarán Lower Camel Case, manteniendo la correspondencia con el nombre utilizado en Java.

### 7.4. Constraint de llave primaria

Las constraints de llave primaria deberán utilizar la convención `pkTabla`.

### 7.5. Constraint de llave foránea

Las constraints de llave foránea deberán utilizar la convención `fkTablaReceptora_TablaOrigen`.

### 7.6. Índices

Los índices deberán utilizar la convención `idxTablaColumna`.

---

## 8. Correspondencia Java - MySQL

Deberá existir una correspondencia clara y consistente entre las entidades Java y las tablas de MySQL.

Los nombres de clases y atributos en Java utilizarán las convenciones propias de Java, mientras que las tablas y columnas de MySQL utilizarán Lower Camel Case.

La transformación entre Upper Camel Case y Lower Camel Case deberá mantenerse uniforme en todos los subsistemas.

La correspondencia entre Java y MySQL deberá documentarse cuando exista alguna excepción a esta regla.

---

## 9. Documentación de clases mediante Javadoc

Toda clase creada por el equipo deberá contener documentación Javadoc antes de la declaración de la clase.

La documentación deberá incluir, como mínimo:

- Propósito de la clase.
- Responsabilidad principal.
- Subsistema al que pertenece cuando sea necesario para comprender su función.
- Relación con otros componentes cuando sea relevante.
- Reglas de negocio importantes que correspondan a la clase.
- Autor.
- Fecha de creación y última modificación.

La documentación deberá mantenerse actualizada cuando cambie la responsabilidad, funcionamiento o relación de la clase con otros componentes.

---

## 10. Documentación de métodos mediante Javadoc

Todo método deberá estar documentado mediante Javadoc.

La documentación Javadoc deberá contener, según corresponda:

- Descripción detallada del funcionamiento.
- `@param` para cada parámetro, indicando su propósito.
- `@return` cuando el método devuelva un valor, explicando el significado del resultado.
- `@throws` para cada excepción relevante que pueda producirse.
- Información sobre modificaciones de estado cuando el método cambie información.
- Información sobre tablas, entidades o componentes involucrados cuando el método interactúe con ellos.
- Reglas de negocio aplicadas por el método cuando existan.
- `@author` con el nombre completo del responsable seguido de su equipo, por ejemplo: `Juan Martínez Equipo 1`.
- Fecha de la creación de la clase.
- Fecha de la última modificación de la clase.

---

## 11. Reglas para la documentación Javadoc

**11.1.** La descripción deberá explicar qué realiza el método y no limitarse a repetir su nombre.

**11.2.** Cada parámetro deberá documentarse mediante `@param` cuando el método reciba parámetros.

**11.3.** El valor devuelto deberá documentarse mediante `@return` cuando exista un valor de retorno.

**11.4.** Las excepciones relevantes deberán documentarse mediante `@throws`.

**11.5.** Si el método modifica información, la documentación deberá indicar qué información modifica y bajo qué condiciones.

**11.6.** Si el método interactúa con la base de datos, deberá indicarse la relación involucrada cuando esta información sea necesaria para poder comprender su funcionamiento.

**11.7.** Si el método aplica una regla de negocio, esta regla deberá quedar documentada.

**11.8.** La documentación deberá utilizar español, excepto los elementos propios de la sintaxis Javadoc, como `@param`, `@return` y `@author`.

**11.9.** La documentación deberá actualizarse junto con los cambios funcionales del método.

**11.10.** El contenido de Javadoc deberá ser específico y suficiente para que otro integrante pueda utilizar o modificar el método sin depender de explicaciones verbales del autor.

---

## 12. Javadoc: estructura mínima

La documentación pública del proyecto deberá utilizar Javadoc como estándar.

La estructura deberá conservar una descripción clara y los elementos `@param`, `@return`, `@throws` y `@author` que correspondan.

Único ejemplo de referencia:

```java
/**
 * Busca un producto mediante su identificador único.
 *
 * @param productoId identificador único del producto que se desea
 *                  consultar.
 * @return producto encontrado o null cuando no existe un producto con
 *         el identificador indicado.
 * @throws SQLException si ocurre un error durante la consulta a la
 *                      base de datos.
 * @author Juan Martínez Equipo 1
 *
 * Fecha creación: 01-01-1521
 *
 * Fecha modificación: 31-12-1678
 *
 */
```

El ejemplo anterior establece únicamente la estructura documental. Cada clase y método deberá documentarse con información propia de su funcionamiento.

---

## 13. SQL y consultas

**13.1.** Las palabras reservadas de SQL deberán escribirse en MAYÚSCULAS.

**13.2.** Las consultas deberán utilizar parámetros en lugar de concatenar directamente datos proporcionados por el usuario.

**13.3.** Nunca se almacenarán contraseñas directamente en el código fuente.

**13.4.** Los datos de conexión deberán mantenerse fuera del código fuente mediante el mecanismo de configuración establecido para el proyecto.

**13.5.** No se utilizará `SELECT *` en código nuevo cuando sea posible especificar las columnas requeridas.

**13.6.** Las consultas deberán solicitar únicamente la información necesaria para la operación realizada.

---

## 14. Transacciones

Las operaciones que modifiquen varias tablas y que conceptualmente formen una sola operación deberán ejecutarse mediante transacciones.

La transacción deberá realizar `COMMIT` únicamente cuando todas las operaciones necesarias hayan finalizado correctamente.

Cuando ocurra un error que impida completar la operación, deberá realizarse `ROLLBACK` para evitar que la base de datos quede en un estado parcialmente actualizado.

Las operaciones de Compras, Ventas e Inventario que dependan entre sí deberán definir claramente sus límites transaccionales.

---

## 15. Reglas para inventario

El inventario será un componente compartido por Compras y Ventas.

Los tres equipos deberán respetar una única definición para:

- Tablas de inventario.
- Columnas de inventario.
- Identificadores.
- Actualización de existencias.
- Tipos de movimiento.
- Reglas para evitar existencias negativas.
- Cancelaciones.
- Devoluciones.
- Ajustes manuales.

Ningún equipo modificará las estructuras o reglas de inventario de forma aislada cuando dichas modificaciones afecten a otro subsistema.

---

## 16. Control de versiones

El proyecto utilizará Git para el control de versiones.

**16.1.** Cada funcionalidad deberá desarrollarse en una rama independiente.

**16.2.** Las correcciones deberán desarrollarse en ramas destinadas específicamente a la corrección correspondiente.

**16.3.** Los nombres de las ramas deberán ser claros, breves y estar escritos en español siempre que no exista una convención técnica que exija otro idioma.

**16.4.** Los commits deberán describir una sola intención.

**16.5.** No se utilizarán mensajes de commit ambiguos.

**16.6.** Antes de integrar una rama se deberá verificar que el proyecto compile y que las pruebas correspondientes sean exitosas.

---

## 17. Integración entre equipos

Antes de realizar un merge se deberá:

- Actualizar la rama con los cambios relevantes.
- Compilar el proyecto.
- Ejecutar las pruebas correspondientes.
- Revisar conflictos.
- Revisar cambios en clases compartidas.
- Revisar cambios en la base de datos.
- Confirmar que los cambios no afecten negativamente a otro subsistema.

Cuando un cambio afecte una tabla, clase, método o contrato compartido, se deberá informar a los equipos correspondientes antes de realizar la integración.

---

## 18. Base de datos compartida

La estructura de la base de datos será considerada un contrato compartido entre los tres equipos.

No se modificarán sin comunicación previa:

- Nombres de tablas.
- Nombres de columnas.
- Tipos de datos.
- Llaves primarias.
- Llaves foráneas.
- Constraints.
- Relaciones entre tablas.

El proyecto deberá mantener scripts SQL versionados para la creación y actualización controlada de la base de datos.

Los scripts deberán poder ejecutarse de forma controlada y reproducible.

---

## 19. Manejo de errores y excepciones

Las excepciones no deberán ignorarse.

Las excepciones deberán:

- Manejarse cuando la aplicación pueda recuperarse de ellas.
- Registrarse cuando sea necesario para diagnosticar el problema.
- Propagarse cuando una capa superior deba decidir cómo responder.
- Generar mensajes comprensibles al usuario cuando corresponda.

No se mostrarán stack traces directamente al usuario final.

---

## 20. Validaciones

Las validaciones importantes deberán realizarse en el nivel apropiado.

Se deberán validar, cuando corresponda:

- Campos obligatorios.
- Cantidades mayores que cero.
- Precios no negativos.
- Existencias no negativas.
- Fechas válidas.
- Identificadores existentes.
- Relaciones válidas entre entidades.
- Reglas específicas del negocio.

Las validaciones de la interfaz no sustituirán las validaciones de la lógica de negocio ni las restricciones de la base de datos.

---

## 21. Pruebas

Bajo la metodología XP se deberán priorizar las pruebas automatizadas.

Cada funcionalidad importante deberá contar con pruebas que contemplen, cuando corresponda:

- Caso exitoso.
- Caso límite.
- Entrada inválida.
- Registro inexistente.
- Error de base de datos.

Los nombres de las pruebas deberán describir el comportamiento esperado y seguir las mismas reglas de idioma establecidas en este documento.

---

## 22. Reglas de revisión de código

Durante la revisión se deberá comprobar:

- Los nombres están escritos en español.
- Las clases utilizan Upper Camel Case.
- Los métodos y atributos utilizan Lower Camel Case.
- Las constantes utilizan MAYUSCULAS_CON_GUION_BAJO.
- Los paquetes están escritos en minúsculas.
- Las clases cuentan con Javadoc.
- Los métodos públicos cuentan con Javadoc.
- Cada clase tiene una responsabilidad clara.
- Las consultas están parametrizadas.
- Las excepciones se manejan adecuadamente.
- No existen contraseñas ni credenciales en el código.
- Las pruebas fueron realizadas.
- Los cambios no rompen otros subsistemas.
- Los cambios de base de datos están documentados.
- No existen nombres ambiguos ni abreviaturas innecesarias.

---

## 23. Rotación de equipos

Cada equipo deberá dejar su subsistema en condiciones de ser entendido por una persona que no participó en su desarrollo.

Cada subsistema deberá contar con un README que explique:

- Propósito del subsistema.
- Forma de ejecución.
- Dependencias.
- Clases principales.
- Tablas utilizadas.
- Relaciones con otros subsistemas.
- Configuración necesaria.
- Ejecución de pruebas.
- Problemas conocidos.

Las decisiones importantes deberán quedar registradas.

---

## 24. Documentación de reuniones Scrum

Los videos de las reuniones Scrum deberán almacenarse en la carpeta designada para las evidencias del proyecto.

El nombre de cada video deberá utilizar exclusivamente la siguiente estructura:

```
aaaa-mm-dd_Equipo
```

Ejemplo:

```
2026-09-01_Equipo1
```

Los videos deberán corresponder al equipo que realizó la reunión y deberán almacenarse únicamente con el nombre establecido. Debe haber un video por cada día hábil en el desarrollo del proyecto, es decir, de lunes a viernes sin contar los siguientes días:

- 15 de septiembre
- 16 de septiembre
- 1 de noviembre
- 2 de noviembre
- 17 de noviembre

---

## 25. Reglas para fechas

El proyecto deberá utilizar un criterio único para fechas y horas.

En Java se utilizará:

- `LocalDate` para fechas.
- `LocalDateTime` para fechas y horas.

En MySQL se utilizará:

- `DATE` para fechas.
- `DATETIME` para fechas y horas.

Cuando el sistema maneje operaciones sensibles al tiempo, la zona horaria utilizada deberá ser la establecida en el punto 3.7.

---

## 26. Reglas para nombres de operaciones

Los verbos utilizados para nombrar operaciones deberán mantener un significado consistente en todo el proyecto.

Se utilizarán:

| Verbo | Significado |
|---|---|
| `crear` | Generar un registro nuevo. |
| `registrar` | Confirmar o guardar una operación de negocio. |
| `obtener` | Recuperar un objeto o dato conocido. |
| `buscar` | Localizar información mediante un criterio. |
| `listar` | Recuperar varios elementos. |
| `actualizar` | Modificar un registro existente. |
| `eliminar` | Quitar un registro conforme a las reglas del sistema. |
| `validar` | Comprobar una condición. |
| `calcular` | Obtener un valor mediante una regla. |
| `consultar` | Recuperar información con intención de lectura. |

No se utilizarán verbos diferentes para representar una misma acción sin una razón funcional o técnica documentada.

---

## 27. Reglas para eliminación de datos

Antes de implementar una eliminación física se deberá determinar si el dato puede eliminarse realmente.

Los registros históricos de compras, ventas y movimientos de inventario deberán conservarse cuando formen parte del historial del sistema.

Cuando las reglas del negocio requieran conservar el historial, se utilizarán mecanismos como estados, cancelaciones o devoluciones en lugar de eliminar físicamente la información.

La decisión deberá corresponder a las reglas del negocio y deberá quedar documentada.

---

## 28. Seguridad básica

Se deberán cumplir las siguientes reglas:

- No guardar contraseñas en el repositorio.
- No compartir credenciales reales en archivos SQL.
- No subir archivos que contengan secretos.
- Utilizar usuarios de base de datos con los permisos mínimos necesarios.
- Utilizar consultas parametrizadas.
- Validar los datos recibidos.
- No confiar exclusivamente en las validaciones de la interfaz.
- Mantener los datos de conexión fuera del código fuente.

---

## 29. Reglas de cambios a este documento

Este documento será considerado una convención compartida por los tres equipos.

Para modificar una regla que afecte a los tres subsistemas se deberá:

1. Proponer el cambio.
2. Explicar el motivo.
3. Revisar el impacto.
4. Acordar el cambio entre los equipos afectados.
5. Actualizar la versión del documento.
6. Comunicar el cambio.
7. Adaptar el código afectado.

Cada modificación deberá registrar como mínimo:

- Versión.
- Fecha.
- Cambio realizado.
- Motivo.
- Equipos afectados.
- Responsable.

---

## 30. Resumen de convenciones obligatorias

### Java

| Elemento | Convención |
|---|---|
| Clase | Upper Camel Case y español. |
| Método | Lower Camel Case y español. |
| Atributo | Lower Camel Case y español. |
| Variable | Lower Camel Case y español. |
| Parámetro | Lower Camel Case y español. |
| Constante | MAYUSCULAS_CON_GUION_BAJO. |
| Paquete | Minúsculas. |
| Enumeración | Upper Camel Case. |
| Valores de enumeración | MAYUSCULAS_CON_GUION_BAJO. |
| Documentación | Javadoc en español. |

### Base de datos

| Elemento | Convención |
|---|---|
| Tabla | Upper Camel Case. |
| Columna | Lower Camel Case. |
| Llave primaria lógica en Java | `nombreTablaId` |
| Constraint de llave primaria | `pkTabla` |
| Constraint de llave foránea | `fkTablaReceptora_TablaOrigen` |
| Constraint UNIQUE | `ukTablaColumna` |
| Constraint CHECK | `ckTablaRegla` |
| Índice | `idxTablaColumna` |

### Reuniones Scrum

Nombre de video: `aaaa-mm-dd_Equipo`

Ejemplo: `2026-08-20_Equipo1.mp4`

### Equipos

**Equipo 1:**

- Becerra Cruz Lizeth Adriana
- González de Santiago Zurisadai
- González Saucedo Oscar
- Islas López Isis Jezabel

**Equipo 2:**

- Álvarez Aguilar Saúl Antonio
- González Cruz Daniel
- Olvera Ramírez Anuar Manuel
- Pérez Flores Roberto Carlos
- Pérez Muñoz Diego

**Equipo 3:**

- Archundia Manzano Ian Aaron
- Torres Peña Jonathan Saul Alí
- Zilli Montero Carlos

---

## 31. Regla final

La prioridad de este documento será la **CONSISTENCIA**.

Las convenciones deberán aplicarse de manera uniforme en los tres subsistemas para evitar diferencias que dificulten la integración, mantenimiento y rotación de equipos.

Ante cualquier situación que no esté contemplada se deberá:

1. No asumir una solución de forma individual cuando pueda afectar a otro equipo.
2. Consultar al equipo correspondiente.
3. Consultar a los tres equipos cuando la decisión afecte a más de un subsistema.
4. Tomar una decisión.
5. Documentar la decisión cuando sea relevante.
6. Aplicarla de manera uniforme.

El objetivo final será que cualquier integrante pueda tomar el código de Compras, Ventas o Inventario y comprenderlo sin depender de la persona que originalmente lo escribió.

---

**Fin del documento**
