# Desafio Votação

Solução para gerenciamento de assembleias, pautas e votações em ambiente cooperativo, desenvolvida em Java com Spring Boot e PostgreSQL. O sistema expõe uma API RESTful para cadastro de entidades, abertura de votações, registro de votos e consulta de resultados, com regras de negócio garantidas no banco de dados.

---

## Visão Geral

O sistema permite:
- Cadastro de associados, assembleias e pautas.
- Associação de pautas a assembleias.
- Abertura de sessões de votação com tempo configurável.
- Registro de votos (Sim/Não) por associados.
- Apuração automática dos resultados.
- Consulta de votações e resultados via API REST.

---

## Regras de Negócio e Restrições

As regras abaixo são garantidas por triggers e constraints no banco de dados (init.sql):

- **Associado:** E-mail e telefone únicos. Não pode ser atualizado se estiver inativo, exceto para reativação. Exclusão apenas inativa o associado (soft delete).
- **Assembleia:** Não pode ser excluída se a data já passou ou se houver pautas associadas com votações não canceladas.
- **Pauta:** Cada pauta tem título e descrição obrigatórios.
- **Votação:** Intervalo entre 1 minuto e 1 dia. Só pode haver uma votação aberta por pauta. Pauta deve estar associada a uma assembleia na data atual. Resultados e contagem de votos não podem ser definidos manualmente. Fechamento automático e apuração dos votos.
- **Voto:** Apenas associados ativos podem votar. Votação deve estar aberta. Um voto por associado por votação. Votos são anonimizados após o registro. Não é permitido alterar voto, associado ou votação após o registro.
- **Associações e Votações:** Não é permitido atualizar ou excluir diretamente registros de associação/votação. Não é permitido excluir associação de pauta/assembleia se houver votação não cancelada.
- **Jobs Automáticos:** Apuração de votos e fechamento de votações são executados a cada minuto via pg_cron.

---

## Tecnologias Utilizadas

- Java 21+
- Spring Boot
- PostgreSQL (com extensão pg_cron)
- Docker e Docker Compose
- Maven
- Spring REST Docs

---

## Como Executar

1. **Pré-requisitos**
   - Java 21+
   - Docker & Docker Compose
   - Maven

2. **Subindo o Banco de Dados**
   ```bash
   docker-compose up -d postgres
   ```

3. **Compilando e Executando a Aplicação**
   Compilar/Gerar imagem docker
   ```bash
   docker build . -t dbserver/voting
   ```
   Executar a aplicação:
   ```bash
   docker-compose up -d voting
   ```

4. **Acessando a API**
   - A API REST estará disponível em: [http://localhost:8080](http://localhost:8080)

---

## Testes e Documentação

- Para rodar os testes e gerar a documentação da API:
  ```bash
  mvn verify
  ```
- A documentação gerada estará em:
  ```
  target/generated-docs/index.html
  ```

---

## Exemplos de Uso da API

### 1. Cadastro de Associado

**Requisição:**
```http
POST /associated
Content-Type: application/json

{
  "name": "João Silva",
  "email": "joao@email.com",
  "phone": "11999999999"
}
```
**Resposta:**
```json
{
  "id": "uuid-gerado",
  "name": "João Silva",
  "email": "joao@email.com",
  "phone": "11999999999",
  "active": true
}
```

---

### 2. Cadastro de Assembleia

**Requisição:**
```http
POST /assembly
Content-Type: application/json

{
  "assemblyDate": "2025-06-19"
}
```
**Resposta:**
```json
{
  "id": "uuid-gerado",
  "assemblyDate": "2025-06-19"
}
```

---

### 3. Cadastro de Pauta

**Requisição:**
```http
POST /subject
Content-Type: application/json

{
  "headline": "Nova Regra de Votação",
  "description": "Descrição detalhada da pauta"
}
```
**Resposta:**
```json
{
  "id": "uuid-gerado",
  "headline": "Nova Regra de Votação",
  "description": "Descrição detalhada da pauta",
  "assemblies": null
}
```

---

### 4. Associação de Pauta a Assembleia

**Requisição:**
```http
POST /subject-assembly
Content-Type: application/json

{
  "subject": { "id": "uuid-da-pauta" },
  "assembly": { "id": "uuid-da-assembleia" }
}
```
**Resposta:**
```json
{
  "subject": { "id": "uuid-da-pauta" },
  "assembly": { "id": "uuid-da-assembleia" }
}
```

---

### 5. Abertura de Votação

**Requisição:**
```http
POST /voting
Content-Type: application/json

{
  "subject": { "id": "uuid-da-pauta" },
  "votingInterval": "PT10M"  // 10 minutos
}
```
**Resposta:**
```json
{
  "id": "uuid-gerado",
  "subject": { "id": "uuid-da-pauta" },
  "votingInterval": "PT10M",
  "openedIn": "2025-06-19T10:00:00Z",
  "closesIn": "2025-06-19T10:10:00Z",
  "status": "OPEN",
  "result": null,
  "votesInFavor": 0,
  "votesAgainst": 0
}
```

---

### 6. Registro de Voto

**Requisição:**
```http
POST /vote
Content-Type: application/json

{
  "voting": { "id": "uuid-da-votacao" },
  "associated": { "id": "uuid-do-associado" },
  "voteValue": true
}
```
**Resposta:**
```json
{
  "id": "uuid-gerado",
  "voting": { "id": "uuid-da-votacao" },
  "voteValue": true
}
```

---

### 7. Consulta de Resultado de Votação

**Requisição:**
```http
GET /voting/{id}
Accept: application/json
```
**Resposta:**
```json
{
  "id": "uuid-da-votacao",
  "subject": { "id": "uuid-da-pauta" },
  "votingInterval": "PT10M",
  "openedIn": "2025-06-19T10:00:00Z",
  "closesIn": "2025-06-19T10:10:00Z",
  "status": "CLOSED",
  "result": "ACCEPTED",
  "votesInFavor": 10,
  "votesAgainst": 2
}
```

---

## Estrutura do Projeto

```
├── src/
│   ├── main/
│   │   ├── java/...
│   │   └── resources/
│   ├── test/
│   │   ├── java/...
│   │   └── resources/
├── database/
│   └── init-scripts/init.sql
├── docker-compose.yml
├── Dockerfile
├── pom.xml
├── run.sh
└── README.md
```

---

## Observações

- As regras de negócio críticas são validadas no banco de dados, garantindo integridade mesmo em cenários de concorrência.
- A documentação da API é gerada durante os testes e pode ser encontrada em ./target/generated-docs/index.html.
- O sistema adota com cron jobs para apuração e fechamento de votações.
- O design da API e sua documentação foram feitos com base nas dependências indicadas via [spring initializr](https://start.spring.io/).