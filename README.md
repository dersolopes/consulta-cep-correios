# Consulta CEP Correios

API Spring Boot para consulta de endereços através de CEP utilizando a API gratuita do ViaCEP, com cache Redis e persistência de histórico.

## Funcionalidades

- Consulta de endereço por CEP
- Suporte a múltiplos formatos: JSON, XML e JSONP
- Cache distribuído com Redis (10 minutos de TTL)
- Histórico de consultas persistido em banco de dados
- Interface web para testes

## Tecnologias

- Java 17
- Spring Boot 3.2.5
- Spring Data JPA
- Spring Data Redis (Cache)
- PostgreSQL (produção)
- H2 (desenvolvimento/testes)
- Redis 7
- Maven
- JUnit 5 + Mockito

## Como Executar

### Com Docker Compose (Recomendado)

1. Clone o repositório
2. Inicie todos os serviços (app, PostgreSQL, Redis):
```bash
docker-compose up -d
```
3. A aplicação estará disponível em http://localhost:8080

**Serviços incluídos:**
- **app**: Aplicação Spring Boot (porta 8080)
- **postgres**: PostgreSQL 16 (porta 5432)
- **redis**: Redis 7 (porta 6379)

**Comandos úteis:**
```bash
# Ver logs
docker-compose logs -f app

# Parar serviços
docker-compose down

# Parar e remover volumes
docker-compose down -v
```

### Sem Docker (H2)

1. Clone o repositório
2. Execute o projeto (usará H2 em memória):
```bash
mvn spring-boot:run
```

### Pré-requisitos (sem Docker)

- Java 17
- Maven 3.6+

## Endpoints

### Consultar Endereço

```
GET /api/endereco/{cep}/{formato}
```

**Parâmetros:**
- `cep`: CEP a ser consultado (8 dígitos)
- `formato`: Formato de resposta (`json`, `xml`, `jsonp`)

**Exemplos:**

```bash
# JSON
curl http://localhost:8080/api/endereco/01001000/json

# XML
curl http://localhost:8080/api/endereco/01001000/xml

# JSONP
curl http://localhost:8080/api/endereco/01001000/jsonp
```

### Histórico por CEP

```
GET /api/endereco/historico/{cep}
```

**Parâmetros:**
- `cep`: CEP para buscar histórico (8 dígitos)

**Exemplo:**
```bash
curl http://localhost:8080/api/endereco/historico/01001000
```

### Histórico Completo

```
GET /api/endereco/historico
```

**Exemplo:**
```bash
curl http://localhost:8080/api/endereco/historico
```

## Interface Web

Acesse `http://localhost:8080` para utilizar a interface web de consulta.

## Exemplo de Resposta (JSON)

```json
{
  "cep": "01001-000",
  "logradouro": "Praça da Sé",
  "complemento": "lado ímpar",
  "bairro": "Sé",
  "localidade": "São Paulo",
  "uf": "SP",
  "ibge": "3550308",
  "gia": "1004",
  "ddd": "11",
  "siafi": "7107"
}
```

## Cache com Redis

O projeto utiliza Redis como cache distribuído para otimizar as consultas de CEP:

**Configuração:**
- TTL de 10 minutos para entradas em cache
- Chave do cache: `cep-formato` (ex: `01001000-json`)
- Valores nulos não são cacheados

**Benefícios:**
- Reduz chamadas à API ViaCEP
- Melhora performance (respostas em ms vs segundos)
- Escalabilidade entre múltiplas instâncias

**Comportamento:**
- Primeira consulta: busca na API ViaCEP e cacheia no Redis
- Consultas subsequentes (dentro de 10 min): retorna do cache
- Após 10 min: cache expira e nova consulta é feita à API

## API Externa

Este projeto utiliza a API gratuita do ViaCEP: https://viacep.com.br/

## Licença

Este projeto é open source e está disponível para uso educacional e comercial.
