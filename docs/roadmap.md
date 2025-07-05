# Roadmap

Este documento resume la planificación del proyecto en tres fases.

## ≤2 semanas
- Definir la arquitectura del **gateway** en [`rest/`](../rest) para unificar las entradas HTTP y futuras gRPC.
- Preparar el módulo de **auditoría** en [`core/src/main/scala/entystal/audit`](../core/src/main/scala/entystal/audit) con registro inmutable.
- Integrar métricas iniciales en [`rest`](../rest) como base del futuro módulo **analytics**.
- Revisar y afinar los workflows de CI/CD.

## ≤2 meses
- Implementar el paquete [`rest/gateway`](../rest) con autenticación y control de acceso centralizado.
- Crear el submódulo [`core/src/main/scala/entystal/audit`](../core/src/main/scala/entystal/audit) para almacenar eventos firmados.
- Añadir el proyecto [`analytics/`](../analytics) para generar reportes en CSV y JSON.
- Ampliar pruebas de integración y añadir tests E2E con Playwright.

## ≤6 meses
- Optimizar el **gateway** con balanceo y caché distribuida.
- Integrar la auditoría con hash en blockchain para mayor trazabilidad.
- Desplegar dashboards de **analytics** con panel gráfico interactivo.
- Documentar nuevas APIs y extender la internacionalización.
