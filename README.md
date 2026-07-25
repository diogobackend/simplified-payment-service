# Simplified Payment Service

API RESTful para simular uma plataforma simplificada de pagamentos.

A aplicação permite cadastrar usuários, manter saldo em carteira e realizar transferências entre usuários comuns e lojistas, seguindo regras de negócio de validação de saldo, autorização externa e notificação de pagamento.

---

## Objetivo

Este projeto implementa um fluxo simplificado de transferência de dinheiro entre usuários.

A regra principal é:

```text
Usuários comuns podem enviar e receber dinheiro.
Lojistas apenas recebem dinheiro.
Toda transferência precisa validar saldo, consultar autorizador externo e registrar a operação de forma transacional.
```

---

## Stack técnica

- Kotlin
- Java 21
- Spring Boot 4
- Gradle Kotlin DSL
- Spring Web MVC
- Spring Data JPA
- Bean Validation
- Flyway
- MySQL
- Spring Boot Actuator
- Springdoc OpenAPI / Swagger
- Docker Compose
- JUnit 5
- MockK
- AssertJ
- JaCoCo
- ktlint

---

## Arquitetura

Este projeto segue **Arquitetura Hexagonal / Ports and Adapters**.

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

## Regras de negócio

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

## Domínio

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

## Integrações externas

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

## API

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

---

## Swagger

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

## Actuator

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

# Como rodar localmente

## 1. Clonar o repositório

```bash
git clone https://github.com/diogobackend/simplified-payment-service.git
cd simplified-payment-service
```

---

## 2. Subir o MySQL

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

## 3. Rodar a aplicação

```bash
./gradlew bootRun
```

A aplicação deve subir em:

```text
http://localhost:8080
```

---

## 4. Validar health check

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

## 5. Validar Swagger

Acessar no navegador:

```text
http://localhost:8080/swagger-ui.html
```

---

## 6. Acessar o banco local

```bash
docker exec -it simplified-payment-mysql mysql -u payment_user -ppayment_pass payment_db
```

Dentro do MySQL:

```sql
SHOW TABLES;
```

---

# Comandos mais usados

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

Principais cenários de teste:

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

# Tratamento de erros

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

# Decisões técnicas

## Aplicação única

A solução foi implementada como uma API única, evitando complexidade desnecessária para o escopo do desafio.

## Arquitetura Hexagonal

A arquitetura foi escolhida para separar domínio, casos de uso, entrada HTTP, persistência e integrações externas.

## MySQL com Flyway

O banco relacional foi escolhido por causa da natureza transacional da transferência.

## BigDecimal para dinheiro

Valores monetários devem ser tratados com `BigDecimal`, evitando `Double` e `Float`.

## Transação no fluxo de transferência

A transferência precisa ser atômica:

```text
ou todo o débito/crédito é concluído, ou nada é persistido.
```

## Notificação fora da regra crítica

A notificação acontece após a transferência concluída.

Falha na notificação não deve desfazer a transferência.

---

# Fluxo de transferência

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
