# Implementação de Cache Redis - Passo a Passo

Este documento descreve o processo de implementação de cache distribuído com Redis na aplicação de consulta de CEP.

## Visão Geral

O Redis foi implementado como cache distribuído para otimizar as consultas de CEP, reduzindo chamadas à API ViaCEP e melhorando a performance da aplicação.

## Passo 1: Adicionar Dependência Redis

**Arquivo:** `pom.xml`

Adicione a dependência do Spring Data Redis:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

**Por que:** O `spring-boot-starter-data-redis` fornece integração automática entre Spring Boot e Redis, incluindo suporte para cache.

## Passo 2: Configurar Redis no application.properties

**Arquivo:** `src/main/resources/application.properties`

Adicione as configurações de conexão e cache:

```properties
# Redis Cache Configuration
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.cache.type=redis
spring.cache.redis.time-to-live=600000
spring.cache.redis.cache-null-values=false
```

**Explicação das configurações:**
- `spring.data.redis.host`: Endereço do servidor Redis
- `spring.data.redis.port`: Porta do Redis (padrão 6379)
- `spring.cache.type=redis`: Define Redis como provedor de cache
- `spring.cache.redis.time-to-live=600000`: TTL de 10 minutos (600000ms)
- `spring.cache.redis.cache-null-values=false`: Não cacheia resultados nulos

## Passo 3: Habilitar Caching na Aplicação

**Arquivo:** `src/main/java/com/dersolopes/consultaCepCorreios/ConsultaCepCorreiosApplication.java`

Adicione a anotação `@EnableCaching`:

```java
@SpringBootApplication
@EnableCaching
public class ConsultaCepCorreiosApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsultaCepCorreiosApplication.class, args);
    }
}
```

**Por que:** A anotação `@EnableCaching` ativa o suporte a cache no Spring Boot, permitindo o uso de anotações como `@Cacheable`.

## Passo 4: Adicionar @Cacheable no Serviço

**Arquivo:** `src/main/java/com/dersolopes/consultaCepCorreios/services/ViaCepService.java`

Adicione a anotação `@Cacheable` no método de consulta:

```java
@Cacheable(value = "cepCache", key = "#cep + '-' + #formato")
@Transactional
public String buscarEnderecoPorCep(String cep, String formato) {
    // ... lógica de consulta à API ViaCEP
}
```

**Explicação:**
- `value = "cepCache"`: Nome do cache no Redis
- `key = "#cep + '-' + #formato"`: Chave única composta por CEP e formato
  - Exemplo: `06322320-json`, `01001000-xml`

## Passo 5: Adicionar Redis ao Docker Compose

**Arquivo:** `docker-compose.yml`

Adicione o serviço Redis:

```yaml
redis:
  image: redis:7-alpine
  container_name: consulta-cep-redis
  ports:
    - "6379:6379"
  volumes:
    - redis_data:/data
  healthcheck:
    test: ["CMD", "redis-cli", "ping"]
    interval: 10s
    timeout: 5s
    retries: 5

volumes:
  redis_data:
    driver: local
```

**Por que:**
- `redis:7-alpine`: Imagem leve do Redis 7
- `ports`: Expõe porta 6379 para acesso externo
- `volumes`: Persistência de dados
- `healthcheck`: Garante que Redis está saudável antes de iniciar a aplicação

## Passo 6: Atualizar Docker Compose com Dependências

**Arquivo:** `docker-compose.yml`

Configure o serviço da aplicação para depender do Redis:

```yaml
app:
  build: .
  container_name: consulta-cep-app
  ports:
    - "8080:8080"
  depends_on:
    postgres:
      condition: service_healthy
    redis:
      condition: service_healthy
  environment:
    SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/consulta_cep_db
    SPRING_DATASOURCE_USERNAME: consulta_user
    SPRING_DATASOURCE_PASSWORD: consulta_pass
    SPRING_DATA_REDIS_HOST: redis
    SPRING_DATA_REDIS_PORT: 6379
  restart: unless-stopped
```

**Por que:**
- `depends_on`: Garante que Redis está saudável antes de iniciar a app
- `SPRING_DATA_REDIS_HOST`: Override do host para nome do container Docker
- `SPRING_DATA_REDIS_PORT`: Override da porta

## Como Funciona o Cache

### Fluxo de Execução

1. **Primeira Consulta:**
   - Requisição chega ao endpoint `/api/endereco/06322320/json`
   - Spring verifica se existe no cache Redis
   - Não existe → chama método `buscarEnderecoPorCep`
   - Método faz requisição à API ViaCEP
   - Resultado é cacheado no Redis com chave `06322320-json`
   - Resultado é retornado ao cliente

2. **Consultas Posteriores (dentro de 10 min):**
   - Requisição chega ao endpoint `/api/endereco/06322320/json`
   - Spring verifica se existe no cache Redis
   - Existe → retorna do cache sem chamar a API
   - Resposta é muito mais rápida (ms vs segundos)

3. **Após 10 minutos:**
   - Cache expira (TTL)
   - Próxima consulta repete o fluxo da primeira consulta

### Estrutura do Cache

**Nome do Cache:** `cepCache`

**Chave:** `{cep}-{formato}`
- Exemplos:
  - `06322320-json`
  - `01001000-xml`
  - `01310200-jsonp`

**Valor:** String JSON retornada pela API ViaCEP

**TTL:** 600000ms (10 minutos)

## Benefícios da Implementação

### Performance
- **Sem cache:** ~500-1000ms por consulta (dependendo da API ViaCEP)
- **Com cache:** ~5-10ms por consulta (leitura do Redis)
- **Ganho:** ~50-200x mais rápido

### Redução de Carga
- Menos requisições à API ViaCEP
- Menos tráfego de rede
- Reduz risco de rate limiting da API externa

### Escalabilidade
- Cache distribuído compartilhado entre instâncias
- Múltiplas instâncias da app usam o mesmo cache
- Consistência de dados entre instâncias

## Testando o Cache

### 1. Iniciar Serviços
```bash
docker-compose up -d
```

### 2. Fazer Primeira Consulta
```bash
curl http://localhost:8080/api/endereco/06322320/json
```
- Tempo: ~500-1000ms (chama API ViaCEP)

### 3. Fazer Segunda Consulta (mesmo CEP)
```bash
curl http://localhost:8080/api/endereco/06322320/json
```
- Tempo: ~5-10ms (retorna do cache)

### 4. Verificar Cache no Redis
```bash
docker exec -it consulta-cep-redis redis-cli
> KEYS *
> GET "cepCache::06322320-json"
```

## Troubleshooting

### Cache não funciona
- Verifique se Redis está rodando: `docker ps`
- Verifique logs da aplicação: `docker-compose logs app`
- Confirme configuração no `application.properties`

### Erro de conexão com Redis
- Verifique se porta 6379 está disponível
- Confirme host no `application.properties` (localhost vs redis)
- Teste conexão: `redis-cli -h localhost -p 6379 ping`

### Cache sempre expira
- Verifique configuração `spring.cache.redis.time-to-live`
- Confirme que não há override em outro lugar

## Próximas Melhorias Possíveis

1. **Cache Manual:** Adicionar endpoint para limpar cache manualmente
2. **TTL Dinâmico:** TTL diferente baseado na frequência do CEP
3. **Cache Multi-nível:** Cache local + Redis para performance extra
4. **Metrics:** Monitorar hit/miss ratio do cache
5. **Warm-up:** Popular cache com CEPs mais comuns ao iniciar

## Resumo

A implementação de cache Redis adicionou:
- Performance significativamente melhorada
- Redução de chamadas à API externa
- Escalabilidade para múltiplas instâncias
- Configuração simples com Spring Boot
- Integração fácil com Docker Compose

O cache é transparente para o usuário final e não requer mudanças nos endpoints ou contrato da API.
