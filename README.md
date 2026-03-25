# 📱 IB2026RafaelRO - Aplicación de Gestión de Facturas

Aplicación Android nativa desarrollada en **Kotlin** con **Jetpack Compose**, enfocada en la gestión integral de facturas de servicios (luz y gas) para usuarios de Iberdrola. El proyecto implementa una arquitectura limpia con patrones MVVM, inyección de dependencias mediante Hilt y persistencia de datos con Room y DataStore.

---

## 🎯 Características Principales

### 📊 Gestión de Facturas
- **Listado de facturas** con filtrado por tipo de servicio (Luz/Gas)
- **Historial completo** agrupado por años
- **Acceso dual a datos**: modo nube (Firebase/API) y modo local (Room)
- **Visualización de detalles** con navegación intuitiva
- **Búsqueda y filtros avanzados** para mejor experiencia de usuario

### 🔐 Gestión de Contratos
- **Activación de contratos** con validación de email
- **Modificación de datos de contacto** (correo electrónico)
- **Gestión de términos y condiciones**
- **Pantallas de confirmación** tras operaciones exitosas

### 👤 Perfil de Usuario
- **Almacenamiento de datos** (nombre, ID, email, teléfono, foto)
- **Persistencia en DataStore** para acceso offline
- **Información de última conexión**

### 📊 Factura Electrónica
- **Acceso a contratos electrónicos**
- **Estado de activación de contratos**
- **Navegación contextual** hacia activación o detalles

### 📈 Analytics & Telemetría
- **Seguimiento de eventos clave** (navegación, clics, cambios de modo)
- **Integración con Firebase Crashlytics** para monitoreo de errores
- **Firebase Analytics** para análisis de comportamiento

### 💡 UX/UI
- **Diseño moderno con Material Design 3**
- **Animaciones fluidas** con Lottie
- **Tema adaptable** y coherente
- **Estado hoisting** para mejor testabilidad
- **Bottom sheets interactivos** (ej: opiniones de usuarios)

---

## 🏗️ Arquitectura

El proyecto sigue una **arquitectura limpia en capas**:

```
app/
├── data/              # Capa de datos
│   ├── di/            # Inyección de dependencias
│   ├── local/         # Base de datos (Room) y DataStore
│   ├── remote/        # Llamadas a APIs (Retrofit)
│   └── repository/    # Implementación de repositorios
├── domain/            # Capa de lógica de negocio
│   ├── model/         # Entidades de dominio
│   ├── network/       # Resultados y excepciones
│   └── usercase/      # Casos de uso
└── ui/                # Capa de presentación
    ├── common/        # Componentes compartidos
    ├── navigation/    # Rutas y navegación
    └── screens/       # Screens y ViewModels
```

### Patrones Implementados

- **MVVM**: State Hoisting para separación UI-Lógica
- **Repository Pattern**: Abstracción de fuentes de datos
- **Use Cases**: Lógica de negocio encapsulada
- **Sealed Classes**: Estados UI type-safe
- **Dependency Injection (Hilt)**: Inyección automática

---

## 🛠️ Stack Tecnológico

### UI & Composables
- **Jetpack Compose** 2024.10.01
- **Material Design 3**
- **Lottie Animations** 6.6.3
- **Coil** 2.6.0 (carga de imágenes)

### Arquitectura & DI
- **Hilt** 2.51.1
- **Navigation Compose** 2.8.3
- **Lifecycle Runtime** 2.8.2

### Persistencia de Datos
- **Room** 2.6.1 (base de datos local)
- **DataStore Preferences** 1.0.0 (preferencias de usuario)

### Networking
- **Retrofit** 2.11.0
- **Gson** (serialización JSON)
- **Firebase** 33.5.1 (Crashlytics, Analytics, Remote Config)

### Testing
- **JUnit 4** 4.13.2
- **MockK** 1.13.5 (mocking)
- **Coroutines Test** 1.7.3
- **Architecture Core Testing** 2.2.0

### Build & Tooling
- **Android Gradle Plugin** 8.7.2
- **Kotlin** 2.1.0
- **KSP** 2.1.0 (procesador de anotaciones)
- **Google Services** 4.4.2

---

## 🚀 Requisitos Previos

- **Android Studio** Jellyfish o superior
- **JDK 17** o superior
- **Gradle 8.7+**
- **Nivel de API mínimo**: 26 (Android 8.0)
- **Nivel de API objetivo**: 35 (Android 15)

---

## 📦 Instalación & Setup

### 1. Clonar el repositorio

```bash
git clone https://github.com/RRO06/IB2026RafaelRO.git
cd IB2026RafaelRO
```

### 2. Configurar Firebase (opcional)

Si el proyecto usa Firebase, coloca el archivo `google-services.json` en:
```
app/google-services.json
```

### 3. Compilar y ejecutar

```bash
# Compilar el proyecto
./gradlew build

# Ejecutar en emulador o dispositivo
./gradlew installDebug
```

---

## 📁 Estructura de Ficheros Importante

### Screens Principales

| Screen | Ruta | Descripción |
|--------|------|-------------|
| **HomeScreen** | `ui/screens/home_facturas/` | Pantalla de inicio con opciones principales |
| **ListadoFacturasScreen** | `ui/screens/list_facturas/` | Listado y filtrado de facturas |
| **ActivarFacturaScreen** | `ui/screens/gestion/` | Activación de contratos |
| **ModificarEmailScreen** | `ui/screens/gestion/` | Modificación de datos de contacto |
| **FilterScreen** | `ui/screens/filt_facturas/` | Filtros avanzados |
| **PerfilScreen** | `ui/screens/perfil/` | Gestión de perfil de usuario |

### ViewModels Principales

- **HomeViewModel**: Gestión de datos y eventos de inicio
- **ListadoFacturasViewModel**: Lógica de listado, filtros y búsqueda
- **GestionViewModel**: Activación y modificación de contratos
- **PerfilViewModel**: Gestión de perfil de usuario

### Repositorios

- **FacturaRepository**: Acceso a datos de facturas (local/remoto)
- **UsuarioRepository**: Gestión de datos de usuario
- **ContratoRepository**: Gestión de contratos

---

## 🔄 Flujo Principal de la Aplicación

```
HomeScreen
    ↓
┌─────────────────────────────────────┐
│  Elegir opción (Nube/Local)         │
└─────────────────────────────────────┘
         ↓
┌──────────────────────────────┐
│  ListadoFacturasScreen       │
│  (Visualización de facturas) │
└──────────────────────────────┘
    ↙                          ↖
┌───────────────────┐    ┌──────────────────┐
│ FacturaElectronica│    │ DetalleFactura   │
│ (Contratos)       │    │ (Detalles)       │
└───────────────────┘    └──────────────────┘
         ↓
┌─────────────────────┐
│ ActivarFactura      │
│ (Activación)        │
└─────────────────────┘
```

---

## 🧪 Testing

El proyecto incluye tests unitarios para ViewModels y lógica de negocio:

```bash
# Ejecutar tests
./gradlew test

# Tests específicos
./gradlew testDebugUnitTest
```

**Archivos de test notables:**
- `ListadoFacturasViewModelTest.kt`: Tests del ViewModel de listado

---

## 📊 Estado de Datos (State Management)

### Sealed Interfaces para Estados UI

```kotlin
// Listado de Facturas
sealed interface ListadoFacturasState {
    data object Loading : ListadoFacturasState
    data class Success(val facturas: List<Factura>) : ListadoFacturasState
    data class Error(val message: String) : ListadoFacturasState
}

// Gestión de Contratos
data class GestionUiState(
    val email: String = "",
    val termsAccepted: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
```

---

## 🔗 Integración con APIs

### Endpoints principales (a través de Retrofit)

- **GET /facturas**: Obtener lista de facturas
- **GET /contratos**: Obtener lista de contratos
- **POST /contratos/{id}/activar**: Activar contrato
- **PUT /usuario/email**: Actualizar email de usuario

---

## 💾 Persistencia Local

### Room Database

Tablas implementadas:
- **facturas**: Almacenamiento local de facturas
- **usuarios**: Datos de perfil
- **contratos**: Información de contratos

### DataStore

Preferencias almacenadas:
- `modo_nube`: Booleano para seleccionar fuente de datos
- `perfil_usuario`: Datos de perfil (nombre, email, etc.)
- `ultima_conexion`: Timestamp de última sesión
- `contador_back`: Para lógica de encuestas

---

## 🎨 Temas y Estilos

El proyecto utiliza **Material Design 3** con:
- Colores corporativos de Iberdrola (verde principal)
- **GreenAplication**: Color de marca en componentes
- Tipografía consistente
- Modo claro y oscuro (soporte nativo de Material Design)

---

## 🐛 Manejo de Errores

- **Try-catch** en funciones suspendidas
- **BaseResult<T>**: Sealed class para resultados (Success/Error)
- **Firebase Crashlytics** para monitoreo de crashes
- **Mensajes de error contextuales** en UI

---

## 📈 Mejoras Futuras

- [ ] Offline-first con sincronización de datos
- [ ] Push notifications para facturas nuevas
- [ ] Exportación de facturas a PDF
- [ ] Gráficos de consumo energético
- [ ] Comparativa de períodos
- [ ] Darkmode completo

---

## 📝 Notas de Desarrollo

### Decisiones Arquitectónicas

1. **State Hoisting**: Los estados se mantienen en ViewModels y se pasan a composables
2. **Actions Objects**: Agrupación de callbacks para mantener firmas limpias
3. **Repository Pattern**: Abstracción de fuentes de datos (local/remoto)
4. **Sealed Classes**: Type-safe state management

### Problemas Conocidos y Soluciones

- **Cambio de schema en Room**: Actualizar versionNumber en la base de datos
- **Ripple effect en Compose**: Usar `LocalIndication.current` explícitamente
- **State consistency**: Validar sincronización entre DataStore y DB

---

## 👨‍💻 Autor

**Rafael R.O.** - Prácticas en Iberdrola 2026

---

## 📄 Licencia

Este proyecto es propiedad de Iberdrola y se utiliza con fines educativos en el programa de prácticas.

---

## 🤝 Contacto & Soporte

Para reportar bugs o sugerencias:
- Email: [tu email de prácticas]
- Proyecto: IB2026RafaelRO

---

**Última actualización**: Marzo 2026
**Versión**: 1.0
**Estado**: En desarrollo activo ✅

