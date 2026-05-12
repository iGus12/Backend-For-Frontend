# Backend-For-Frontend (BFF) - Sanos y Salvos

El **BFF (Backend-For-Frontend)** es la puerta de enlace principal y el orquestador del sistema **Sanos y Salvos**. Su objetivo es recibir todas las peticiones desde el Frontend en React y distribuirlas hacia los microservicios correspondientes (Mascotas, Usuarios, Auth), centralizando la comunicación y aplicando patrones de tolerancia a fallos.

---

#  Funcionalidades Principales
* *Enrutamiento Centralizado:** Actúa como único punto de entrada (puerto `8085`) para el Frontend, evitando que la interfaz tenga que conocer las IPs o puertos de cada microservicio interno.
* *Tolerancia a Fallos (Circuit Breaker):** Implementación de patrón Circuit Breaker para evitar fallos en cascada. Si un microservicio interno se cae, el BFF maneja el error de forma controlada sin colapsar el sistema completo.
* *Orquestación y Agregación:** Capacidad para unificar respuestas de múltiples microservicios antes de enviarlas al cliente.
* *Gestión de CORS:** Configurado de forma centralizada para permitir la comunicación segura con el cliente web (React).

---

# Stack Tecnológico
* *Framework:** Spring Boot (Java)
* *Gestor de Dependencias:** Maven
* *Patrones de Diseño:**  BFF, Circuit Breaker (Resilience4j / Spring Cloud Circuit Breaker)
* *Arquitectura:** Microservicios

---

#  Configuración y Ejecución local

# Requisitos previos
* Java Development Kit (JDK) 17 o superior.
* Maven instalado.
* Tener corriendo los microservicios internos (`Ms_Mascotas` en 8080, `Ms_Auth` en 8081, `Ms_Usuarios` en 8083).
* 
