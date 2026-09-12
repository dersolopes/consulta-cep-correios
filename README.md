# Consulta CEP Correios

API Spring Boot para consulta de endereços através de CEP utilizando a API gratuita do ViaCEP.

## Funcionalidades

- Consulta de endereço por CEP
- Suporte a múltiplos formatos: JSON, XML e JSONP
- Interface web para testes

## Tecnologias

- Java 17+
- Spring Boot
- Maven
- HttpClient (Java 11+)

## Como Executar

### Pré-requisitos

- Java 17 ou superior
- Maven 3.6+

### Passos

1. Clone o repositório
2. Execute o projeto:
```bash
mvn spring-boot:run
```

A aplicação iniciará na porta 8080.

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

## API Externa

Este projeto utiliza a API gratuita do ViaCEP: https://viacep.com.br/

## Licença

Este projeto é open source e está disponível para uso educacional e comercial.
