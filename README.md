# DeustoRestaurant

Aplicación web de gestión de reservas de restaurante. Proyecto universitario desarrollado con Spring Boot + H2 + frontend estático.

---

## Guía de funcionamiento

### Roles de usuario

La aplicación tiene tres tipos de usuario, cada uno con acceso a funcionalidades distintas:

| Rol | Descripción |
|-----|-------------|
| `CLIENTE` | Puede registrarse, iniciar sesión, crear y cancelar sus reservas, y enviar mensajes |
| `CAMARERO` | Gestiona las reservas asignadas a su turno y se comunica con los clientes |
| `GERENTE` | Administra el restaurante: aforo, camareros, reservas globales y mensajería |

---

### 1. Registro e inicio de sesión

1. Abre `http://localhost:8080` en el navegador.
2. Pulsa **Registrarse** en la barra de navegación.
3. Rellena el formulario (nombre, apellidos, email, contraseña, teléfono). El rol se asigna automáticamente como `CLIENTE`.
4. Tras registrarte serás redirigido al **dashboard**.
5. Para iniciar sesión en el futuro, pulsa **Iniciar sesión** e introduce tu email y contraseña.

> Los roles `CAMARERO` y `GERENTE` solo pueden ser asignados directamente en la base de datos o a través de la API.

---

### 2. Panel de cliente (dashboard)

Una vez logueado como cliente verás dos secciones principales:

**Mis Reservas**
- Consulta todas tus reservas activas.
- Crea una nueva reserva eligiendo fecha, turno (Comida / Cena) y número de comensales.
- Cancela cualquier reserva en estado `PENDIENTE` o `CONFIRMADA`.

**Mensajes**
- Envía mensajes al personal del restaurante.
- Consulta los mensajes recibidos y los que aún no has leído.

---

### 3. Panel de camarero

El camarero accede al mismo dashboard con opciones adaptadas a su rol:

- **Mis turnos**: visualiza las reservas asignadas a su nombre, filtradas por fecha y turno (Comida o Cena).
- **Gestión de reservas**: puede cambiar el estado de una reserva a `COMPLETADA` o `NO_SHOW` una vez finalizado el servicio.
- **Mensajes**: recibe y responde mensajes de clientes.

---

### 4. Panel de gerente

El gerente tiene visibilidad total sobre el restaurante:

- **Restaurante**: actualiza datos del local (dirección, teléfono, horarios) y los aforos máximos por turno.
- **Camareros**: consulta el listado de camareros asignados y puede asignar nuevos usuarios al restaurante.
- **Reservas**: ve todas las reservas del restaurante y puede asignar un camarero a cada una.
- **Mensajes**: se comunica con clientes y camareros.

---

### 5. Turnos y aforo

Cada día el restaurante ofrece dos turnos:

- **Comida**: de 13:00 a 16:00. Aforo controlado por `aforoMaximoComida`.
- **Cena**: de 20:00 a 23:00. Aforo controlado por `aforoMaximoCena`.

Si el número de reservas activas en un turno alcanza el aforo máximo, el sistema rechaza nuevas reservas para ese turno y fecha con un error `400 Bad Request`.

---

### 6. Estados de una reserva

Una reserva sigue este ciclo de vida:

```
PENDIENTE → CONFIRMADA → COMPLETADA
                       → NO_SHOW
         → CANCELADA  (en cualquier momento antes del servicio)
```

---

### 7. Datos de prueba (DataInitializer)

Al arrancar la aplicación se crean automáticamente los siguientes usuarios de prueba:

| Nombre | Email | Contraseña | Rol |
|--------|-------|------------|-----|
| Carlos García | carlos@test.com | 1234 | CLIENTE |
| Ana López | ana@test.com | 1234 | CLIENTE |
| Marta Ruiz | marta@test.com | 1234 | CLIENTE |
| Pedro Sanz | pedro@test.com | 1234 | CAMARERO |
| Laura Vega | laura@test.com | 1234 | CAMARERO |
| Elena Torres | elena@test.com | 1234 | GERENTE |

---

## Tecnologías

- Java 21 + Spring Boot 3.x
- Gradle
- H2 (base de datos en memoria)
- JPA / Hibernate + Lombok
- JaCoCo (cobertura de tests)
- JUnit 5 + Mockito

---

## Arrancar el proyecto

```bash
cd "C:\Proyecto procesos\trabajo_procesos_Larry_Herrero"
./gradlew bootRun
```

La app arranca en `http://localhost:8080`.  
Consola H2 disponible en `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:testdb`).

---

## Ejecutar los tests

```bash
./gradlew test
```

Ejecuta todos los tests unitarios (Mockito) y de integración (MockMvc).

---

## Cobertura de tests con JaCoCo

### Generar el informe

```bash
./gradlew test jacocoTestReport
```

Esto ejecuta los tests y genera automáticamente el informe de cobertura.

### Ver el informe en el navegador

Una vez generado, abre el siguiente archivo en tu navegador:

```
build/reports/jacoco/html/index.html
```

En Windows puedes hacer doble clic sobre ese archivo directamente desde el explorador de archivos.

### Verificar el umbral mínimo de cobertura (70%)

```bash
./gradlew test jacocoTestCoverageVerification
```

El build fallará si la cobertura global cae por debajo del **70%**. Útil para CI/CD.

### Todo en un solo comando

```bash
./gradlew test jacocoTestReport jacocoTestCoverageVerification
```

---

## Estructura del proyecto

```
src/
├── main/
│   ├── java/com/deustoRestaurant/DRestaurant/
│   │   ├── config/        # CORS, WebConfig
│   │   ├── dao/           # Repositorios JPA
│   │   ├── dto/           # Request y Response DTOs
│   │   ├── entity/        # Entidades JPA + Enums
│   │   ├── facade/        # Controladores REST (@RestController)
│   │   └── service/       # Lógica de negocio
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/deustoRestaurant/DRestaurant/
        ├── facade/        # Tests de integración (MockMvc)
        └── service/       # Tests unitarios (Mockito)

frontend/                  # HTML/CSS/JS (servido por Spring Boot)
├── index.html
├── login.html
├── registro.html
├── dashboard.html
├── css/
└── js/
```

---

## Endpoints principales

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/usuarios` | Registrar usuario |
| POST | `/api/usuarios/login` | Login |
| GET | `/api/usuarios/rol/{rol}` | Usuarios por rol |
| POST | `/api/restaurantes` | Crear restaurante |
| GET | `/api/restaurantes/activos` | Restaurantes activos |
| POST | `/api/reservas` | Crear reserva |
| PUT | `/api/reservas/{id}/cancelar` | Cancelar reserva |
| PUT | `/api/reservas/{id}/estado` | Cambiar estado |
| POST | `/api/mensajes` | Enviar mensaje |
| GET | `/api/mensajes/recibidos/{id}` | Mensajes recibidos |
| PUT | `/api/mensajes/{id}/leido` | Marcar como leído |
