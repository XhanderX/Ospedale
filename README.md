# Ospedale

Proyecto Java Swing para la gestion basica de un hospital, refactorizado con una
estructura MVC y separacion por responsabilidades.

## Integrantes

- Andrés Barandica 2038
- Andrés Issa 2038
- Manuel Polo 2039

## Tecnologias usadas

- Java 21
- Apache NetBeans 25
- Java Swing
- JSON con `org.json`
- FlatLaf

## Estructura principal

- `packagee.model`: entidades y enums del dominio.
- `packagee.dto`: objetos de transferencia enviados desde controladores hacia vistas.
- `packagee.mapper`: conversion entre modelos y DTO.
- `packagee.storage`: repositorios, almacenamiento en memoria y carga JSON.
- `packagee.validator`: validaciones reutilizables.
- `packagee.service`: reglas de servicio, como disponibilidad de citas.
- `packagee.controller`: casos de uso y respuestas para la vista.
- `packagee.view`: pantallas Swing.
- `packagee.response`: respuesta estandar con estado, mensaje y datos.

## Arquitectura

El proyecto aplica MVC:

- Las vistas capturan datos de formularios, llaman controladores y muestran respuestas.
- Los controladores validan, coordinan servicios/repositorios y devuelven DTO.
- Los modelos representan las reglas y datos del dominio.
- Los repositorios abstraen la persistencia simulada y la carga desde JSON.

Tambien se aplican principios SOLID:

- SRP: paquetes separados por responsabilidad.
- OCP: controladores y servicios se apoyan en interfaces de validacion y repositorio.
- LSP: usuarios concretos heredan de `User` sin romper su uso base.
- ISP: repositorios y validadores pequenos por dominio.
- DIP: controladores dependen de abstracciones y reciben dependencias desde `AppContext`.

## Ejecucion

1. Abrir el proyecto en NetBeans.
2. Verificar que el JDK seleccionado sea Java 21.
3. Verificar que las librerias de `lib` esten en el classpath.
4. Ejecutar la clase principal `packagee.Main`.

## Datos

Los datos base se cargan desde archivos JSON mediante `HospitalStorage` y los
repositorios definidos en `packagee.storage`.
