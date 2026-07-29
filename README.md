# Simplified Payment Service

API RESTful para simular uma plataforma simplificada de pagamentos

A aplicação permite realizar transferências entre usuários comuns e lojistas, seguindo regras de negócio de validação de saldo, autorização externa e notificação de pagamento.

Origem do desafio:
https://github.com/PicPay/picpay-desafio-backend

## Sumário

- [1 - Objetivo](#1---objetivo)
- [2 - Stack técnica](#2---stack-técnica)
- [3 - Modelagem do banco de dados - DER](#3---modelagem-do-banco-de-dados---der)
- [4 - Arquitetura](#4---arquitetura)
- [5 - Regras de negócio](#5---regras-de-negócio)
- [6 - Domínio](#6---domínio)
- [7 - Integrações externas](#7---integrações-externas)
- [8 - API](#8---api)
- [9 - Observabilidade](#9---observabilidade)
- [10 - Swagger](#10---swagger)
- [11 - Actuator](#11---actuator)
- [12 - Como rodar localmente](#12---como-rodar-localmente)
- [13 - Comandos mais usados](#13---comandos-mais-usados)
- [14 - Principais cenários de teste](#14---principais-cenários-de-teste)
- [15 - Tratamento de erros](#15---tratamento-de-erros)
- [16 - Fluxo de Transação e notificação da transferência](#16---fluxo-de-transação-e-notificação-da-transferência)
- [17 - Fluxo de transferência](#17---fluxo-de-transferência)
- [18 - Cenários de Teste via Swagger](#18---cenários-de-teste-via-swagger)
    - [18.1 - Cenários de sucesso](#181---cenários-de-sucesso)
    - [18.2 - Cenários de erro](#182---cenários-de-erro)
- [19 - Observação sobre autorização externa](#19---observação-sobre-autorização-externa)
- [20 - Testes locais com WireMock](#20---testes-locais-com-wiremock)
- [21 - Cenários de falha com WireMock](#21---cenários-de-falha-com-wiremock)
---

## 1 - Objetivo

Implementar um fluxo simplificado de transferência de dinheiro entre usuários.

Regra principal:

```text
Usuários comuns podem enviar e receber dinheiro.
Lojistas apenas recebem dinheiro.
Toda transferência precisa validar saldo, consultar autorizador externo e registrar a operação de forma transacional.
```

---

## 2 - Stack técnica

- Kotlin
- Java 21
- Spring Boot, Web, JPA
- Flyway
- MySQL
- Swagger
- Docker Compose
- WireMock /stubs
- JUnit 5
- Testes unitários e de integração
- JaCoCo /relatorio de cobertura nos testes
- ktlint
- Logs estruturados, TraceId / SpanId
- OpenTelemetry

---
## 3 - Modelagem do banco de dados - DER
![doc1.png](docs/images/doc1.png)
- Um usuário possui uma única carteira, e cada carteira pertence a um único usuário -> `1:1`

- Um usuário pode participar de várias transferências, seja como pagador ou receptor. Cada transferência, por sua vez, está vinculada apenas aos dois usuários envolvidos(pagador e receptor) -> `1:N`
## 4 - Arquitetura

Ultilizei **Arquitetura Hexagonal / Ports and Adapters**.
![doc2.png](docs/images/doc2.png)
Regra principal:

```text
O domínio não deve depender de Spring, banco de dados, HTTP, clients externos ou qualquer detalhe de infraestrutura.
```

Estrutura esperada:

```text
src/main/kotlin/com/simplifiedpayment/
├── SimplifiedPaymentServiceApplication.kt
├── core/
│   ├── common/
│   │   └── messages/
│   ├── domain/
│   │   ├── model/
│   │   ├── exception/
│   │   └── valueobject/
│   ├── port/
│   │   ├── input/
│   │   └── output/
│   └── usecase/
└── app/
    ├── adapter/
    │   ├── input/
    │   │   └── web/
    │   │       ├── controllers/
    │   │       ├── handler/
    │   │       ├── mappers/
    │   │       ├── requests/
    │   │       ├── responses/
    │   │       └── swagger/
    │   └── output/
    │       ├── client/
    │       └── persistence/
    │           ├── entity/
    │           ├── mapper/
    │           └── repository/
    └── configuration/
```

---

## 5 - Regras de negócio

- Usuários possuem nome completo, documento, e-mail, senha e carteira.
- Documento e e-mail devem ser únicos.
- Usuários comuns podem enviar dinheiro.
- Usuários comuns podem receber dinheiro.
- Lojistas podem receber dinheiro.
- Lojistas não podem enviar dinheiro.
- O pagador precisa ter saldo suficiente.
- Antes de concluir a transferência, a aplicação consulta um autorizador externo.
- A transferência deve ser transacional.
- Em caso de falha, o saldo deve ser preservado.
- Após uma transferência aprovada, o recebedor deve ser notificado.
- Falha na notificação não deve desfazer uma transferência já concluída.

---

## 6 - Domínio

### User

Representa um usuário da plataforma.

Campos principais:

```text
userId
fullName
document
email
password
type
createdAt
updatedAt
```

---

### Wallet

Representa a carteira de um usuário.

Campos principais:

```text
walletId
userId
balance
createdAt
updatedAt
```

---

### Transfer

Representa uma transferência entre dois usuários.

Campos principais:

```text
transferId
payerId
payeeId
value
status
createdAt
updatedAt
```

---

### UserType

Tipos possíveis:

```text
COMMON
MERCHANT
```

---

### TransferStatus

Status possíveis:

```text
CREATED
AUTHORIZED
COMPLETED
FAILED
```

---

## 7 - Integrações externas

### Serviço autorizador

Antes de concluir a transferência, a aplicação consulta um serviço externo de autorização.

```http
GET https://util.devi.tools/api/v2/authorize
```

Se a autorização for negada ou o serviço estiver indisponível, a transferência não deve ser concluída.

---

### Serviço de notificação

Após uma transferência concluída, a aplicação tenta notificar o recebedor.

```http
POST https://util.devi.tools/api/v1/notify
```

A notificação pode falhar sem desfazer a transferência, desde que o pagamento já tenha sido concluído.

---

## 8 - API

A API principal de transferência segue o contrato:

```http
POST /transfer
Content-Type: application/json
```

Payload:

```json
{
  "value": 100.0,
  "payer": 4,
  "payee": 15
}
```

Resposta esperada em caso de sucesso:

```json
{
  "transferId": "4e0f7d25-8f79-44ab-9f7e-6c4f91b41f2a",
  "payer": 4,
  "payee": 15,
  "value": 100.0,
  "status": "COMPLETED"
}
```
## 9 - Observabilidade

A aplicação possui logs estruturados para facilitar a análise do fluxo de transferência.

Os logs registram:

- `traceId`
- `spanId`
- Nome da classe
- Nome do método
- Parâmetros de entrada
- Retorno do método

Exemplo de log:

```text
traceId=..., spanId=..., C=TransferController, M=transfer, parameters={arg0=TransferRequest(...)}, return=TransferResponse(...)
```
![img_51.png](docs/images/img_51.png)

## 10 - Swagger

A documentação completa da API estará disponível via Swagger.

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

---

## 11 - Actuator

A aplicação expõe endpoints operacionais.

Health:

```http
GET /actuator/health
```

Metrics:

```http
GET /actuator/metrics
```

Prometheus:

```http
GET /actuator/prometheus
```

---

# 12 - Como rodar localmente

## Clonar o repositório

```bash
git clone https://github.com/diogobackend/simplified-payment-service.git
cd simplified-payment-service
```

---

## Subir o MySQL

```bash
docker compose up -d
```

Verificar container:

```bash
docker ps
```

Container esperado:

```text
simplified-payment-mysql
```

---

## Rodar a aplicação

```bash
./gradlew bootRun
```

A aplicação deve subir em:

```text
http://localhost:8080
```

---

##  Validar health check

```bash
curl http://localhost:8080/actuator/health
```

Resposta esperada:

```json
{
  "status": "UP"
}
```

---

##  Validar Swagger

Acessar no navegador:

```text
http://localhost:8080/swagger-ui.html
```

---

## Acessar o banco local

```bash
docker exec -it simplified-payment-mysql mysql -u payment_user -ppayment_pass payment_db
```

Dentro do MySQL:

```sql
SHOW TABLES;
```

---

## 13 - Comandos mais usados

## Subir infraestrutura local

```bash
docker compose up -d
```

---

## Parar infraestrutura local

```bash
docker compose down
```

---

## Parar e remover volumes

```bash
docker compose down -v
```

---

## Ver logs dos containers

```bash
docker compose logs -f
```

---

## Ver logs do MySQL

```bash
docker compose logs -f mysql
```

---

## Rodar aplicação

```bash
./gradlew bootRun
```

---

## Rodar build completo

```bash
./gradlew clean build
```

---

## Rodar testes

```bash
./gradlew test
```

---

## Rodar testes com relatório JaCoCo

```bash
./gradlew clean test jacocoTestReport
```

---

## Abrir relatório JaCoCo

```bash
xdg-open build/reports/jacoco/test/html/index.html
```
Deixei com 100% de cobertura nos testes unitários e de integração
![img.png](docs/images/jacoco.png)

---

## Rodar ktlint check

```bash
./gradlew ktlintCheck
```

---

## Corrigir formatação com ktlint

```bash
./gradlew ktlintFormat
```

---

## Rodar validação geral antes de commit

```bash
./gradlew ktlintFormat
./gradlew ktlintCheck
./gradlew clean test jacocoTestReport
./gradlew clean build
```

---

## Limpar build local

```bash
./gradlew clean
```

---

## Ver dependências do projeto

```bash
./gradlew dependencies
```

---

## 14 - Principais cenários de teste

- transferência com sucesso;
- pagador inexistente;
- recebedor inexistente;
- lojista tentando enviar dinheiro;
- saldo insuficiente;
- autorização externa negada;
- falha no autorizador externo;
- transferência transacional;
- notificação com sucesso;
- falha na notificação sem rollback do pagamento.

---

## 15 - Tratamento de erros

A API deve retornar erros padronizados.

Exemplo:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Payer does not have enough balance",
  "path": "/transfer"
}
```

Erros previstos:

```text
400 Bad Request
404 Not Found
409 Conflict
422 Unprocessable Entity
500 Internal Server Error
```

---



## 16 - Fluxo de Transação e notificação da transferência

A transferência precisa ser atômica:

```text
ou todo o débito/crédito é concluído, ou nada é persistido.
```

### Notificação fora da regra crítica

A notificação acontece após a transferência concluída.

Falha na notificação não deve desfazer a transferência.

---

## 17 - Fluxo de transferência

```text
Recebe requisição POST /transfer
      |
      v
Valida payload
      |
      v
Busca pagador
      |
      v
Busca recebedor
      |
      v
Valida se pagador não é lojista
      |
      v
Valida saldo do pagador
      |
      v
Consulta autorizador externo
      |
      v
Debita saldo do pagador
      |
      v
Credita saldo do recebedor
      |
      v
Registra transferência
      |
      v
Confirma transação
      |
      v
Envia notificação ao recebedor
```
---

## 18 - Cenários de Teste via Swagger

Esta seção descreve os principais cenários de teste da API de transferência.

Endpoint utilizado:

```http
POST /transfer
```

Swagger:

```text
http://localhost:8080/swagger-ui.html
```

### Massa de dados local

A aplicação possui uma massa local criada por migration para facilitar os testes manuais.

Resumo da massa:

| Tipo | Quantidade |
|---|---:|
| Usuários comuns com carteira | 17 |
| Lojistas com carteira | 13 |
| Usuários comuns sem carteira | 8 |
| Lojistas sem carteira | 8 |
| Carteiras de usuários comuns | 17 |
| Carteiras de lojistas | 13 |

> Usuário do tipo `COMMON` pode enviar dinheiro.  
> Usuário do tipo `MERCHANT` não pode enviar dinheiro, apenas receber.

---

## 18.1 - Cenários de sucesso

### Cenário 1 — Usuário comum(CPF) paga um lojista(CNPJ)

**Objetivo:** validar uma transferência comum para lojista.

**Payload:**

```json
{
  "value": 100,
  "payer": 1,
  "payee": 101
}
```

**Resultado esperado:**

```text
HTTP 201 Created
status: COMPLETED
```

**Efeito esperado no banco:**

| Usuário | Saldo antes | Saldo depois |
|---|---:|---:|
| Ana Silva | 1000.00 | 900.00 |
| Padaria São João | 0.00 | 100.00 |


**Evidência da Requisição**

![img_3.png](docs/images/img_3.png)

**Evidência Banco**

***Evidência da transferência no banco***
![img_5.png](docs/images/img_5.png)

***Valores antes da transferência***
![img_7.png](docs/images/img_7.png)
![img_2.png](docs/images/img_2.png)

***Valores após a transferência***
![img_4.png](docs/images/img_4.png)

---

### Cenário 2 — Usuário comum paga outro usuário comum, ambos CPF

**Objetivo** validar uma transferência entre dois usuários comuns.

**Payload**

```json
{
  "value": 50,
  "payer": 2,
  "payee": 3
}
```

**Resultado esperado:**

```text
HTTP 201 Created
status: COMPLETED
```

**Efeito esperado no banco:**

| Usuário | Saldo antes | Saldo depois |
|---|---:|---:|
| Bruno Costa | 500.00 | 450.00 |
| Carla Mendes | 100.00 | 150.00 |

**Evidência da Requisição**

![img_10.png](docs/images/img_10.png)

**Evidência Banco**

***Evidência da transferência no banco***
![img_11.png](docs/images/img_11.png)

***Valores antes da transferência***
![img_8.png](docs/images/img_8.png)
![img_9.png](docs/images/img_9.png)

***Valores após a transferência***
![img_13.png](docs/images/img_13.png)

---

### Cenário 3 — Transferência com centavos

**Objetivo:** validar transferência com valor decimal.

**Payload:**

```json
{
  "value": 25.50,
  "payer": 7,
  "payee": 102
}
```

**Resultado esperado:**

```text
HTTP 201 Created
status: COMPLETED
```

**Efeito esperado no banco:**

| Usuário | Saldo antes | Saldo depois |
|---|---:|---:|
| Helena Martins | 75.50 | 50.00 |
| Farmácia Central | 250.00 | 275.50 |

**Evidência da Requisição**

![img_17.png](docs/images/img_17.png)

**Evidência Banco**

***Evidência da transferência no banco***
![img_18.png](docs/images/img_18.png)

***Valores antes da transferência***
![img_15.png](docs/images/img_15.png)
![img_14.png](docs/images/img_14.png)

***Valores após a transferência***
![img_19.png](docs/images/img_19.png)

---

## 18.2 - Cenários de erro

### Cenário 4 — Lojista(CNPJ) tentando enviar dinheiro para pessoa comun(CPF)

**Objetivo:** validar que lojista não pode ser pagador.

**Payload:**

```json
{
  "value": 100,
  "payer": 101,
  "payee": 1
}
```

**Resultado esperado:**

```text
HTTP 422 Unprocessable Content
message: Merchant cannot send money
```
**Evidência Banco**
![img_20.png](docs/images/img_20.png)


**Evidência da Requisição**

![img_21.png](docs/images/img_21.png)

---

### Cenário 5 — Saldo insuficiente

**Objetivo:** validar que a transferência não é permitida quando o pagador não possui saldo suficiente.

**Payload:**

```json
{
  "value": 999999,
  "payer": 1,
  "payee": 101
}
```

**Resultado esperado:**

```text
HTTP 400 Bad Request
message: Wallet balance is insufficient
```
**Evidência Banco**
![img_24.png](docs/images/img_24.png)


**Evidência da Requisição**

![img_23.png](docs/images/img_23.png)
---

### Cenário 6 — Usuário com saldo zero tentando pagar

**Objetivo:** validar que usuário com saldo zero não consegue realizar transferência.

**Payload:**

```json
{
  "value": 1,
  "payer": 5,
  "payee": 101
}
```

**Resultado esperado:**

```text
HTTP 400 Bad Request
message: Wallet balance is insufficient
```
**Evidência Banco**
![img_25.png](docs/images/img_25.png)


**Evidência da Requisição**

![img_26.png](docs/images/img_26.png)
---

### Cenário 7 — Pagador inexistente

**Objetivo:** validar erro quando o pagador não existe na base.

**Payload:**

```json
{
  "value": 100,
  "payer": 999,
  "payee": 101
}
```

**Resultado esperado:**

```text
HTTP 404 Not Found
message: User not found with id: 999
```
**Evidência Banco**
![img_27.png](docs/images/img_27.png)


**Evidência da Requisição**

![img_28.png](docs/images/img_28.png)
---

### Cenário 8 — Recebedor inexistente

**Objetivo:** validar erro quando o recebedor não existe na base.

**Payload:**

```json
{
  "value": 100,
  "payer": 1,
  "payee": 999
}
```

**Resultado esperado:**

```text
HTTP 404 Not Found
message: User not found with id: 999
```
**Evidência Banco**
![img_30.png](docs/images/img_30.png)


**Evidência da Requisição**

![img_31.png](docs/images/img_31.png)
---

### Cenário 9 — Pagador sem carteira

**Objetivo:** validar erro quando o pagador existe, mas não possui carteira.

**Payload:**

```json
{
  "value": 100,
  "payer": 201,
  "payee": 101
}
```

**Resultado esperado:**

```text
HTTP 404 Not Found
message: Wallet not found with userId: 201
```
**Evidência Banco**
![img_32.png](docs/images/img_32.png)
![img_33.png](docs/images/img_33.png)


**Evidência da Requisição**

![img_34.png](docs/images/img_34.png)
---

### Cenário 10 — Recebedor comum(CPF) sem carteira

**Objetivo:** validar erro quando o recebedor comum existe, mas não possui carteira.

**Payload:**

```json
{
  "value": 100,
  "payer": 1,
  "payee": 201
}
```

**Resultado esperado:**

```text
HTTP 404 Not Found
message: Wallet not found with userId: 201
```
**Evidência Banco**
![img_36.png](docs/images/img_36.png)
![img_37.png](docs/images/img_37.png)


**Evidência da Requisição**

![img_38.png](docs/images/img_38.png)
---

### Cenário 11 — Recebedor lojista(CNPJ) sem carteira

**Objetivo:** validar erro quando o lojista existe, mas não possui carteira.

**Payload:**

```json
{
  "value": 100,
  "payer": 1,
  "payee": 301
}
```

**Resultado esperado:**

```text
HTTP 404 Not Found
message: Wallet not found with userId: 301
```
**Evidência Banco**
![img_39.png](docs/images/img_39.png)
![img_40.png](docs/images/img_40.png)


**Evidência da Requisição**

![img_41.png](docs/images/img_41.png)
---

### Cenário 12 — Pagador e recebedor são o mesmo usuário

**Objetivo:** validar que uma pessoa não pode transferir dinheiro para ela mesma.

**Payload:**

```json
{
  "value": 100,
  "payer": 1,
  "payee": 1
}
```

**Resultado esperado:**

```text
HTTP 400 Bad Request
message: Transfer payer and payee must be different
```
**Evidência Banco**
![img_42.png](docs/images/img_42.png)


**Evidência da Requisição**

![img_43.png](docs/images/img_43.png)
---

### Cenário 13 — Valor zerado

**Objetivo:** validar erro de entrada quando o valor da transferência é zero.

**Payload:**

```json
{
  "value": 0,
  "payer": 1,
  "payee": 101
}
```

**Resultado esperado:**

```text
HTTP 400 Bad Request
message contendo: value
```

**Evidência da Requisição**

![img_44.png](docs/images/img_44.png)

---

### Cenário 14 — Valor negativo

**Objetivo:** validar erro de entrada quando o valor da transferência é negativo.

**Payload:**

```json
{
  "value": -10,
  "payer": 1,
  "payee": 101
}
```

**Resultado esperado:**

```text
HTTP 400 Bad Request
message contendo: value
```
**Evidência da Requisição**

![img_45.png](docs/images/img_45.png)
---

### Cenário 15 — Pagador inválido

**Objetivo:** validar erro de entrada quando o identificador do pagador é inválido.

**Payload:**

```json
{
  "value": 100,
  "payer": 0,
  "payee": 101
}
```

**Resultado esperado:**

```text
HTTP 400 Bad Request
message contendo: payer
```
**Evidência da Requisição**

![img_46.png](docs/images/img_46.png)
---

### Cenário 16 — Recebedor inválido

**Objetivo:** validar erro de entrada quando o identificador do recebedor é inválido.

**Payload:**

```json
{
  "value": 100,
  "payer": 1,
  "payee": 0
}
```

**Resultado esperado:**

```text
HTTP 400 Bad Request
message contendo: payee
```
**Evidência da Requisição**

![img_47.png](docs/images/img_47.png)
---

## 19 - Observação sobre autorização externa

A API utiliza um serviço externo para autorização da transferência.

Caso um cenário de sucesso retorne:

```text
HTTP 403 Forbidden
message: Transfer was not authorized
```

isso significa que o autorizador externo negou ou não respondeu corretamente.

Nesse caso, a transferência não será concluída e os saldos não serão alterados

## 20 - Testes locais com WireMock

A aplicação possui suporte a stubs locais usando WireMock para simular os serviços externos de autorização e notificação.

Esse modo evita depender dos serviços externos reais durante testes manuais pelo Swagger ou Postman.

### Quando usar

Suba a aplicação com o profile `local` quando quiser testar a aplicação com serviços externos simulados:

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

Com esse profile ativo, a aplicação utiliza o WireMock local para simular:

- autorização da transferência
- envio de notificação

---

### Evidências que o WireMock subiu corretamente


URL que valida os stubs carregados

```text
http://localhost:8089/__admin/mappings
```
![img.png](docs/images/img.png)


Stub de autorização aprovado

```text
http://localhost:8089/api/v2/authorize
```
![img_1.png](docs/images/aimg_1.png)

Chamada isolada pelo stub ao serviço de notificação que retorna 204 NO Content
![img_2.png](docs/images/aimg_2.png)

### Evidência da transferência pelo Swagger

```text
http://localhost:8080/swagger-ui.html
```
![img_4.png](docs/images/aimg_4.png)


### Evidência da notificação recebida no WireMock

```text
http://localhost:8089/__admin/requests
```
![img_6.png](docs/images/aimg_6.png)


### Evidência nos logs da aplicação

Após executar a transferência pelo Swagger, o terminal da aplicação registrou o fluxo completo.

Logs validados:

```text
C=NotificationClientAdapter, M=notify
C=TransferMoneyUseCase, M=transfer
C=TransferController, M=transfer
```
![img_7.png](docs/images/aimg_7.png)


---

### Resultado final validado

Com o profile `local` ativo e o WireMock rodando, foi validado que:

```text
- a aplicação iniciou com o profile local
- os stubs foram carregados no WireMock
- o autorizador simulado retornou authorization=true
- a transferência foi executada pelo Swagger
- a API retornou 201 CREATED
- a transferência foi concluída com status COMPLETED
- a aplicação enviou a notificação para o WireMock
- o WireMock recebeu POST /api/v1/notify com status 204
- os logs da aplicação registraram o fluxo completo com traceId e spanId
```

## 21 - Cenários de falha com WireMock

Além do fluxo de sucesso, também é possível validar cenários de falha usando os stubs locais do WireMock.

Esses cenários são úteis para comprovar o comportamento da aplicação quando os serviços externos de autorização ou notificação retornam erro ou negam a operação.

---

###  1 — Autorização negada

```bash
./gradlew bootRun --args='--spring.profiles.active=local --external.authorization.url=http://localhost:8089/api/v2/authorize/denied'
```
![img_9.png](docs/images/aimg_9.png)
![img_10.png](docs/images/aimg_10.png)


Esse cenário confirma que, quando o serviço externo nega a autorização, a transferência não é concluída.

---

### 2 — Erro no serviço de autorização


```bash
./gradlew bootRun --args='--spring.profiles.active=local --external.authorization.url=http://localhost:8089/api/v2/authorize/error'
```
![img_11.png](docs/images/aimg_11.png)
![img_12.png](docs/images/aimg_12.png)

Esse cenário confirma que, se o autorizador externo falhar, a aplicação trata a transferência como não autorizada.

---

### 3 — Erro no serviço de notificação

```bash
./gradlew bootRun --args='--spring.profiles.active=local --external.notification.url=http://localhost:8089/api/v1/notify/error'
```

A transferência é criada normalmente, mesmo quando o serviço externo de notificação retorna erro.

![img_13.png](docs/images/aimg_13.png)
![img_14.png](docs/images/aimg_14.png)
---

Evidência nos logs
![img_16.png](docs/images/aimg_16.png)
Isso evidencia que, a aplicação não desfaz a transferência quando a notificação falha, mantendo o status COMPLETED.



### Resultado validado

Com os stubs de falha do WireMock, foram validados os seguintes comportamentos:

```text
- autorização negada retorna 403
- erro no autorizador retorna 403
- erro na notificação não impede a transferência
- transferência continua com status COMPLETED quando apenas a notificação falha
- os logs registram o fluxo e as exceções com traceId e spanId
```
