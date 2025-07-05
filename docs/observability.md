# Observabilidad

Este proyecto expone métricas y trazas para facilitar el análisis en tiempo real.

## Visualizar trazas con Jaeger

1. **Levantar Jaeger** mediante Docker:
   ```bash
   docker run -d --name jaeger \
     -e COLLECTOR_OTLP_ENABLED=true \
     -p 16686:16686 -p 14250:14250 \
     jaegertracing/all-in-one:1.54
   ```
2. Arrancar el servidor REST con `sbt "rest/run"`.
3. Acceder a `http://localhost:16686` y buscar el servicio `entystal-rest` para ver las trazas.

## Métricas de negocio

La ruta `/metrics` expone estadísticas en formato Prometheus. Puedes usar Prometheus o Grafana para visualizarlas.

1. **Ejemplo de scrape** en `prometheus.yml`:
   ```yaml
   scrape_configs:
     - job_name: 'entystal'
       static_configs:
         - targets: ['localhost:8080']
   ```
2. Tras levantar Prometheus, consulta <http://localhost:9090> o configura Grafana para obtener gráficas.
