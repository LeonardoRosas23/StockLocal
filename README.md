# StockLocal

Aplicación móvil Android desarrollada en Kotlin para la gestión y control de inventario local.

## Descripción

StockLocal es una aplicación enfocada en pequeños negocios que necesitan llevar un control sencillo, rápido y eficiente de su inventario. La aplicación permite registrar productos, controlar entradas y salidas de stock, visualizar alertas de inventario bajo y exportar información relevante en formato CSV.

El proyecto fue desarrollado utilizando arquitectura moderna para Android, aplicando buenas prácticas de desarrollo móvil, persistencia local y manejo de APIs.

---

## Características principales

* Registro de negocio y autenticación mediante PIN.
* Almacenamiento seguro del PIN usando hash SHA-256.
* Gestión completa de productos:

    * Agregar productos.
    * Editar productos.
    * Eliminar productos.
    * Visualizar detalles.
* Control de movimientos:

    * Entradas de inventario.
    * Salidas de inventario.
    * Validación de stock disponible.
* Dashboard principal con:

    * Resumen de inventario.
    * Alertas de stock bajo.
    * Navegación rápida.
* Filtro de productos por categoría.
* Filtro de movimientos por fecha.
* Exportación CSV de:

    * Inventario.
    * Historial de movimientos.
* Persistencia local offline.
* Integración con API externa para sugerencia automática de categorías.
* Manejo de errores y validaciones de formularios.

---

## Tecnologías utilizadas

### Lenguaje

* Kotlin

### UI

* Jetpack Compose
* Material Design 3

### Arquitectura

* MVVM (Model - View - ViewModel)
* Repository Pattern
* StateFlow

### Persistencia local

* Room Database
* DataStore Preferences

### Inyección de dependencias

* Hilt

### Consumo de APIs

* Retrofit
* OkHttp
* Gson

### Procesamiento asíncrono

* Kotlin Coroutines

---

## Arquitectura del proyecto

El proyecto sigue una arquitectura MVVM para separar responsabilidades y facilitar el mantenimiento del código.

### Capas principales

#### UI

Contiene pantallas, componentes Compose y lógica de interacción visual.

#### ViewModel

Gestiona el estado de la interfaz y la comunicación con los repositorios.

#### Repository

Centraliza el acceso a datos locales y remotos.

#### Data

Incluye:

* Room Database
* DAO
* Modelos
* Servicios Retrofit

---

## Persistencia y funcionamiento offline

La aplicación utiliza Room Database para almacenar productos y movimientos localmente, permitiendo que el sistema funcione incluso sin conexión a internet.

La configuración del negocio y el PIN de acceso se almacenan mediante DataStore.

---

## Seguridad

El PIN de acceso no se almacena en texto plano.

Se implementó hash SHA-256 para proteger la información de autenticación del usuario.

---

## Integración con API externa

Inicialmente se planeó utilizar Open Food Facts para la obtención automática de categorías de productos. Sin embargo, debido a problemas de disponibilidad y compatibilidad durante el desarrollo, se optó por utilizar DummyJSON como alternativa para mantener la integración de Retrofit y las funcionalidades de autocompletado.

---

## Exportación de información

La aplicación permite exportar información en formato CSV para facilitar respaldos y control administrativo.

### Exportaciones disponibles

#### Inventario

Incluye:

* Nombre
* Categoría
* Cantidad
* Precio de referencia
* Moneda

#### Movimientos

Incluye:

* Producto
* Tipo de movimiento
* Cantidad
* Fecha y hora

---

## Validaciones implementadas

* Restricción de cantidades negativas.
* Validación de campos obligatorios.
* Validación de PIN.
* Prevención de salidas con stock insuficiente.
* Prevención de múltiples registros de negocio.
* Manejo de errores de red.

---

## Estructura general del proyecto

```text
app/
 ├── data/
 │    ├── local/
 │    ├── remote/
 │    └── repository/
 │
 ├── di/
 │
 ├── ui/
 │    ├── dashboard/
 │    ├── inventory/
 │    ├── login/
 │    ├── movements/
 │    ├── register/
 │    └── settings/
 │
 └── MainActivity.kt
```

---

## Instalación y ejecución

### Requisitos

* Android Studio
* JDK 11
* Android SDK
* Dispositivo Android o emulador

### Clonar repositorio

```bash
git clone https://github.com/LeonardoRosas23/StockLocal.git
```

### Entrar al proyecto

```bash
cd StockLocal
```

### Abrir en Android Studio

1. Abrir Android Studio.
2. Seleccionar "Open".
3. Elegir la carpeta del proyecto.
4. Esperar la sincronización de Gradle.

### Ejecutar aplicación

Desde Android Studio:

```text
Run > Run 'app'
```

O mediante terminal:

```bash
./gradlew assembleDebug
```

---

## Estado del proyecto

Proyecto funcional y estable.

Funciones principales implementadas y probadas en dispositivo físico.

---

## Autores

* González Gallegos José Jesús
* Gómez Juárez Alan Fabricio
* Rosas Pérez Leonardo Daniel

---
