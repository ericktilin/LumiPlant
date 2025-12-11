# LumiPlant: gestor de Plantas y Sensor de Luz

> **Proyecto Integrador - Desarrollo de Aplicaciones Móviles**
>
> **Semestre:** 4°D DSM
> **Fecha de entrega:** 11 de Diciembre

---

## Equipo de Desarrollo

| Nombre Completo | Rol / Tareas Principales | Usuario GitHub |
| :--- | :--- | :--- |
| Erick Jahir Flores Lopez | Backend, Retrofit | 20243ds101@utez.edu.mx |
| Berenice Jaanai Maldonado Camaño | Backend  | 20243ds115@utez.edu.mx |
| Luis Enrique Solano Achota | Backend | 20243ds180@utez.edu.mx |

---

## Descripción del Proyecto

**¿Qué hace la aplicación?**
LumiPlant es una aplicación diseñada para la gestión de plantas de interior y exterior. Permite a los usuarios registrar sus plantas favoritas y,
lo más importante, monitorear si las condiciones de luz ambiental actual son adecuadas para su salud, basándose en la luz requerida por cada especie.

**¿Qué problema resuelve?**
Ayuda a los dueños de plantas a evitar los errores más comunes: exceso o falta de luz.
Proporciona recomendaciones claras ("Mover a zona más iluminada", "Nivel Óptimo") 
contrastando las necesidades de la planta con la medición en tiempo real del sensor del dispositivo.

**Objetivo:**
Demostrar la implementación de una arquitectura robusta en Android utilizando servicios web y hardware del dispositivo.

---

## Stack Tecnológico y Características

Este proyecto ha sido desarrollado siguiendo estrictamente los lineamientos de la materia:

* **Lenguaje:** Kotlin 100%.
* **Interfaz de Usuario:** Jetpack Compose (Diseño modular y reactivo).
* **Arquitectura:** MVVM (Model-View-ViewModel) estricta.
* **Conectividad (API REST):** Retrofit.
    * **GET:** Obtiene la lista completa de plantas registradas desde el servidor Python.
    * **POST:** Envía una nueva planta (Nombre, Tipo, Lux Requerida y **URI de la Imagen**) al servidor.
    * **PUT:** Actualiza los datos de una planta existente.
    * **DELETE:** Elimina una planta específica mediante su ID.
* **Sensores Integrados:**
    1.  **Sensor de Luz Ambiental:** Es el sensor principal, utilizado para leer el nivel de Lux actual del entorno y generar recomendaciones.
    2.  **Cámara:** Se utiliza para permitir al usuario asignar una fotografía real a cada planta registrada.
