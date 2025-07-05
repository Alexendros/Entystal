# Configuración Zero Trust

Estos ejemplos muestran cómo habilitar mTLS y obtener secretos desde HashiCorp Vault.

## Servidor REST con mTLS

Define las siguientes variables de entorno antes de lanzar el servidor:

```bash
export HTTPS_KEYSTORE_PATH=/ruta/servidor.p12
export HTTPS_KEYSTORE_PASSWORD=changeit
export HTTPS_TRUSTSTORE_PATH=/ruta/clientes_ca.p12
export HTTPS_TRUSTSTORE_PASSWORD=changeit
```

Con ello `RestServer` exigirá certificados de cliente válidos.

## HashiCorp Vault

El servicio `SecretManager` lee los tokens y credenciales ejecutando:

```bash
export VAULT_ADDR=http://127.0.0.1:8200
export VAULT_TOKEN=<token>
```

En Vault deben existir las rutas `secret/data/jwt` y `secret/data/database` con las claves `secret`, `username` y `password` respectivamente.
